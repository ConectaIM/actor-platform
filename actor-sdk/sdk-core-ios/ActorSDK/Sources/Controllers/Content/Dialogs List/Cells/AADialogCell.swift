//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import UIKit

final class AADialogCell: AATableViewCell, AABindedCell {
    
    fileprivate let binder = AABinder()
    
    // Binding data type
    
    public typealias BindData = ACDialog
    
    // Hight of cell
    
    open static func bindedCellHeight(_ table: AAManagedTable, item: ACDialog) -> CGFloat {
        return 76
    }
    
    // Cached design
    
    fileprivate static let counterBgImage = Imaging
        .imageWithColor(ActorSDK.sharedActor().style.dialogCounterBgColor, size: CGSize(width: 18, height: 18))
        .roundImage(18)
        .resizableImage(withCapInsets: UIEdgeInsetsMake(9, 9, 9, 9))
    fileprivate lazy var dialogTextActiveColor = ActorSDK.sharedActor().style.dialogTextActiveColor
    fileprivate lazy var dialogTextColor = ActorSDK.sharedActor().style.dialogTextColor
    fileprivate lazy var dialogStatusSending = ActorSDK.sharedActor().style.dialogStatusSending
    fileprivate lazy var dialogStatusRead = ActorSDK.sharedActor().style.dialogStatusRead
    fileprivate lazy var dialogStatusReceived = ActorSDK.sharedActor().style.dialogStatusReceived
    fileprivate lazy var dialogStatusSent = ActorSDK.sharedActor().style.dialogStatusSent
    fileprivate lazy var dialogStatusError = ActorSDK.sharedActor().style.dialogStatusError
    fileprivate lazy var dialogAvatarSize = ActorSDK.sharedActor().style.dialogAvatarSize
    fileprivate lazy var chatIconClock = ActorSDK.sharedActor().style.chatIconClock
    fileprivate lazy var chatIconCheck2 = ActorSDK.sharedActor().style.chatIconCheck2
    fileprivate lazy var chatIconCheck1 = ActorSDK.sharedActor().style.chatIconCheck1
    fileprivate lazy var chatIconError = ActorSDK.sharedActor().style.chatIconError
    
    // Views

    fileprivate var cellRenderer: AABackgroundCellRenderer<AADialogCellConfig, AADialogCellLayout>!
    
    open let avatarView = AAAvatarView()
    open let titleView = YYLabel()
    open let dialogTypeView = UIImageView()
    open let messageView = YYLabel()
    open let typingView = YYLabel()
    
    open let dateView = YYLabel()
    open let statusView = UIImageView()
    open let counterView = YYLabel()
    open let counterViewBg = UIImageView()
        
    // Binding Data
    
    fileprivate var bindedItem: ACDialog?

    private var typingMessage = ""
    
    public override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
    
        cellRenderer = AABackgroundCellRenderer<AADialogCellConfig, AADialogCellLayout>(renderer: cellRender, receiver: cellApply)
        
        titleView.displaysAsynchronously = true
        titleView.ignoreCommonProperties = true
//        titleView.fadeOnAsynchronouslyDisplay = true
        titleView.clearContentsBeforeAsynchronouslyDisplay = true
    
        messageView.displaysAsynchronously = true
        messageView.ignoreCommonProperties = true
//        messageView.fadeOnAsynchronouslyDisplay = true
        messageView.clearContentsBeforeAsynchronouslyDisplay = true
        
        typingView.displaysAsynchronously = true
        typingView.ignoreCommonProperties = true
        typingView.clearContentsBeforeAsynchronouslyDisplay = true
        
        dateView.displaysAsynchronously = true
        dateView.ignoreCommonProperties = true
//        dateView.fadeOnAsynchronouslyDisplay = true
        dateView.clearContentsBeforeAsynchronouslyDisplay = true
        
        counterView.displaysAsynchronously = true
        counterView.ignoreCommonProperties = true
//        counterView.fadeOnAsynchronouslyDisplay = true
        counterView.clearContentsBeforeAsynchronouslyDisplay = true

        counterViewBg.image = AADialogCell.counterBgImage
        
        statusView.contentMode = .center
        
        self.contentView.addSubview(avatarView)
        self.contentView.addSubview(titleView)
        self.contentView.addSubview(dialogTypeView)
        self.contentView.addSubview(messageView)
        self.contentView.addSubview(typingView)
        self.contentView.addSubview(dateView)
        self.contentView.addSubview(statusView)
        self.contentView.addSubview(counterViewBg)
        self.contentView.addSubview(counterView)
    }

    public required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open func bind(_ item: ACDialog, table: AAManagedTable, index: Int, totalCount: Int) {

        //
        // Checking dialog rebinding
        //
        if (item.peer.peerType.ordinal() == ACPeerType.private().ordinal()) {
            let user = Actor.getUserWithUid(item.peer.peerId)
            binder.bind(Actor.getTypingWithUid(item.peer.peerId), valueModel2: user.getPresenceModel(), closure:{ (typing:JavaLangBoolean?, presence:ACUserPresence?) -> () in
                if (typing != nil && typing!.booleanValue()) {
                    self.typingMessage = Actor.getFormatter().formatTyping()
                    self.typingView.isHidden = false
                    self.messageView.isHidden = true
                } else {
                    self.typingView.isHidden = true
                    self.messageView.isHidden = false
                }
                self.cellRenderer.cancelRender()
                self.setNeedsLayout()
            })
        } else if (item.peer.peerType.ordinal() == ACPeerType.group().ordinal()) {
            let group = Actor.getGroupWithGid(item.peer.peerId)
            binder.bind(Actor.getGroupTyping(withGid: group.getId()), valueModel2: group.membersCount, valueModel3: group.getPresenceModel(), closure: { (typingValue:IOSIntArray?, membersCount: JavaLangInteger?, onlineCount:JavaLangInteger?) -> () in
                if (group.isMemberModel().get().booleanValue()) {
                    if (typingValue != nil && typingValue!.length() > 0) {
                        self.typingView.textColor = self.appStyle.navigationSubtitleActiveColor
                        if (typingValue!.length() == 1) {
                            let uid = typingValue!.int(at: 0);
                            let user = Actor.getUserWithUid(uid)
                            self.typingMessage = Actor.getFormatter().formatTyping(withName: user.getNameModel().get())
                        } else {
                            self.typingMessage = Actor.getFormatter().formatTyping(withCount: typingValue!.length());
                        }
                        self.typingView.isHidden = false
                        self.messageView.isHidden = true
                    } else {
                        self.typingView.isHidden = true
                        self.messageView.isHidden = false
                    }
                    self.cellRenderer.cancelRender()
                    self.setNeedsLayout()
                }
            })
        }
        
        // Nothing changed
        if bindedItem == item {
            return
        }
        
        var isRebind: Bool = false
        if let b = bindedItem {
            if b.peer.isEqual(item.peer) {
                isRebind = true
            }
        }
        self.bindedItem = item
        
        //
        // Avatar View
        //
        avatarView.bind(item.dialogTitle, id: Int(item.peer.peerId), avatar: item.dialogAvatar)
        
        // Forcing Async Rendering.
        // This flag can became false when cell was resized
        if !titleView.displaysAsynchronously {
            titleView.displaysAsynchronously = true
        }
        
        if !messageView.displaysAsynchronously {
            messageView.displaysAsynchronously = true
        }
        
        if !typingView.displaysAsynchronously {
            typingView.displaysAsynchronously = true
        }

        
        // Reseting Text Layout on new peer binding
        if !isRebind {
            avatarView.alpha = 0
            titleView.alpha = 0
            messageView.alpha = 0
            typingView.alpha = 0
            statusView.alpha = 0
            dateView.alpha = 0
            counterView.alpha = 0
            counterViewBg.alpha = 0
        } else {
            titleView.clearContentsBeforeAsynchronouslyDisplay = false
            messageView.clearContentsBeforeAsynchronouslyDisplay = false
            typingView.clearContentsBeforeAsynchronouslyDisplay = false
            dateView.clearContentsBeforeAsynchronouslyDisplay = false            
            counterView.clearContentsBeforeAsynchronouslyDisplay = false
        }
        
        //
        // Message State
        //
        
        if item.senderId != Actor.myUid() {
            self.statusView.isHidden = true
        } else {
            if item.isRead() {
                self.statusView.tintColor = dialogStatusRead
                self.statusView.image = chatIconCheck2
                self.statusView.isHidden = false
            } else if item.isReceived() {
                self.statusView.tintColor = dialogStatusReceived
                self.statusView.image = chatIconCheck2
                self.statusView.isHidden = false
            } else {
                self.statusView.tintColor = dialogStatusSent
                self.statusView.image = chatIconCheck1
                self.statusView.isHidden = false
            }
        }
        
       

        // Cancelling Renderer and forcing layouting to start new rendering
        cellRenderer.cancelRender()
        setNeedsLayout()
    }
    
    open override func prepareForReuse() {
        super.prepareForReuse()
        binder.unbindAll()
    }
    
    open func addImageDialogType(_ image: UIImage!){
        dialogTypeView.image = image
        let dialogTypeFrame = CGRect(x: 76, y: 17, width: 18, height: 18)
        dialogTypeView.frame = dialogTypeFrame
        self.titleView.left = self.titleView.left+20
        
    }
    
    open func removeImageDialogType(){
        dialogTypeView.image = UIImage()
        let dialogTypeFrame = CGRect(x: 76, y: 17, width: 18, height: 18)
        dialogTypeView.frame = dialogTypeFrame
        self.titleView.left = 76
    }
 
    
    open override func layoutSubviews() {
        super.layoutSubviews()

        // We expect height == 76;
        let width = self.contentView.frame.width
        let leftPadding = CGFloat(76)
        let padding = CGFloat(14)
        
        //
        // Avatar View
        //
        let avatarPadding = padding + (50 - dialogAvatarSize) / 2
        avatarView.frame = CGRect(x: avatarPadding, y: avatarPadding, width: dialogAvatarSize, height: dialogAvatarSize)
        
        
        //
        // Title
        //
        let titleFrame = CGRect(x: leftPadding, y: 16, width: width - leftPadding - /*paddingRight*/(padding + 50), height: 21)
        UIView.performWithoutAnimation {
            self.titleView.frame = titleFrame
        }
        
        //
        // Status Icon
        //
        if (!self.statusView.isHidden) {
            statusView.frame = CGRect(x: leftPadding, y: 44, width: 20, height: 18)
        }
        
        
        //
        // Rest of Elements are layouted on the last phase
        //
        if let binItem = bindedItem {
            let config = AADialogCellConfig(
                item: binItem,
                isStatusVisible: !statusView.isHidden,
                titleWidth: titleFrame.width,
                contentWidth: width,
                typingText:self.typingMessage)
            
            if cellRenderer.requestRender(config) {
                
                // Disable async rendering on frame resize to avoid blinking on resize
                titleView.displaysAsynchronously = false
                titleView.clearContentsBeforeAsynchronouslyDisplay = false
                messageView.displaysAsynchronously = false
                messageView.clearContentsBeforeAsynchronouslyDisplay = false
                typingView.displaysAsynchronously = false
                typingView.clearContentsBeforeAsynchronouslyDisplay = false
                dateView.displaysAsynchronously = false
                dateView.clearContentsBeforeAsynchronouslyDisplay = false
                counterView.displaysAsynchronously = false
                counterView.clearContentsBeforeAsynchronouslyDisplay = false
            }
            
            
            //
            //Image Type
            //
            
            let isBot = binItem.isBot
            let isChannel = binItem.isChannel
            
            if(binItem.peer.peerType == ACPeerType.group()){
                if(isChannel){
                    addImageDialogType(UIImage.bundled("ic_channel"))
                }else {
                    addImageDialogType(UIImage.bundled("ic_group"))
                }
            }else if(binItem.peer.peerType == ACPeerType.private()){
                if(isBot){
                    addImageDialogType(UIImage.bundled("ic_robot"))
                }else{
                    removeImageDialogType()
                }
            }else{
                removeImageDialogType()
            }
            
        }
    }
    
    fileprivate func cellRender(_ config: AADialogCellConfig) -> AADialogCellLayout! {
        //
        // Title Layouting
        //
        
        let title = NSMutableAttributedString(string: config.item.dialogTitle)
        title.yy_font = UIFont.mediumSystemFontOfSize(17)
        title.yy_color = appStyle.dialogTitleColor
        let titleContainer = YYTextContainer(size: CGSize(width: config.titleWidth, height: 1000))
        titleContainer.maximumNumberOfRows = 1
        titleContainer.truncationType = .end
        let titleLayout = YYTextLayout(container: titleContainer, text: title)!
        
        //
        // Message Status
        //
        
        var messagePadding: CGFloat = 0
        if config.isStatusVisible {
            messagePadding = 22
        }
        
        
        //
        // Counter
        //
        
        var unreadPadding: CGFloat = 0
        let counterLayout: YYTextLayout?
        if config.item.unreadCount > 0 {
            let counter = NSMutableAttributedString(string: "\(config.item.unreadCount)")
            counter.yy_font = UIFont.systemFont(ofSize: 14)
            counter.yy_color = appStyle.dialogCounterColor
            counterLayout = YYTextLayout(containerSize: CGSize(width: 1000, height: 1000), text: counter)!
            unreadPadding = max(counterLayout!.textBoundingSize.width + 8, 18)
        } else {
            counterLayout = nil
        }
        
        
        //
        // Message
        //
        
        let message = NSMutableAttributedString(string: Actor.getFormatter().formatDialogText(config.item))
        message.yy_font = UIFont.systemFont(ofSize: 16)
        if config.item.messageType.ordinal() != ACContentType.text().ordinal() {
            message.yy_color = dialogTextActiveColor
        } else {
            message.yy_color = dialogTextColor
        }
        let messageWidth = config.contentWidth - 76 - 14 - messagePadding - unreadPadding
        let messageContainer = YYTextContainer(size: CGSize(width: messageWidth, height: 1000))
        messageContainer.maximumNumberOfRows = 1
        messageContainer.truncationType = .end
        let messageLayout = YYTextLayout(container: messageContainer, text: message)!
        
        
        //
        // Typing
        //
        let typingText = NSMutableAttributedString(string:config.typingText)
        typingText.yy_font = UIFont.systemFont(ofSize: 16)
        typingText.yy_color = dialogTextActiveColor
        

        let typingContainer = YYTextContainer(size: CGSize(width: messageWidth, height: 1000))
        typingContainer.maximumNumberOfRows = 1
        typingContainer.truncationType = .end
        let typingLayout = YYTextLayout(container: typingContainer, text: typingText)!
        
        //
        // Date
        //
        
        let dateStr: String
        if config.item.date > 0 {
            dateStr = Actor.getFormatter().formatShortDate(config.item.date)
        } else {
            dateStr = ""
        }
        let dateAtrStr = NSMutableAttributedString(string: dateStr)
        dateAtrStr.yy_color = appStyle.dialogDateColor
        dateAtrStr.yy_font = UIFont.systemFont(ofSize: 14)
        let dateContainer = YYTextContainer(size: CGSize(width: 60, height: 1000))
        let dateLayout = YYTextLayout(container: dateContainer, text: dateAtrStr)!
        
        return AADialogCellLayout(titleLayout: titleLayout, messageLayout: messageLayout, typingLayout:typingLayout, messageWidth: messageWidth, counterLayout: counterLayout, dateLayout: dateLayout)
    }
    
    fileprivate func cellApply(_ render: AADialogCellLayout!) {
        
        //
        // Avatar
        //
        
        presentView(avatarView)
        
        
        //
        // Title
        //
        self.titleView.textLayout = render.titleLayout
        presentView(titleView)
        
        
        let leftPadding: CGFloat
        if isEditing {
            leftPadding = 8
        } else {
            leftPadding = 14
        }
        
        
        //
        // Date
        //
        
        dateView.textLayout = render.dateLayout
        let dateWidth = render.dateLayout.textBoundingSize.width
        dateView.frame = CGRect(x: contentView.width - dateWidth - leftPadding, y: 18, width: dateWidth, height: 18)
        presentView(dateView)
        
        
        //
        // Message
        //
        
        var padding: CGFloat = 76
        if !statusView.isHidden {
            padding += 22
        }
        let messageViewFrame = CGRect(x: padding, y: 44, width: render.messageWidth, height: 22)
        UIView.performWithoutAnimation {
            self.messageView.frame = messageViewFrame
        }
        self.messageView.textLayout = render.messageLayout
        if !self.messageView.isHidden{
            presentView(messageView)
        }
        
        
        //
        // Typing
        //
        
        let typingViewFrame = CGRect(x: padding, y: 44, width: render.messageWidth, height: 22)
        UIView.performWithoutAnimation {
            self.typingView.frame = typingViewFrame
        }
        typingView.textLayout = render.typingLayout
        if !self.typingView.isHidden{
             presentView(typingView)
        }
        
        //
        // Message State
        //
        if !self.statusView.isHidden {
            presentView(self.statusView)
        }
        
        
        //
        // Counter
        //
        
        if render.counterLayout != nil {
            self.counterView.textLayout = render.counterLayout
            
            let textW = render.counterLayout!.textBoundingSize.width
            let unreadW = max(textW + 8, 18)
            
            counterView.frame = CGRect(x: contentView.width - leftPadding - unreadW + (unreadW - textW) / 2, y: 44, width: textW, height: 18)
            counterViewBg.frame = CGRect(x: contentView.width - leftPadding - unreadW, y: 44, width: unreadW, height: 18)
            
            presentView(counterView)
            presentView(counterViewBg)
        } else {
            
            dismissView(counterView)
            dismissView(counterViewBg)
        }
    }
    
    fileprivate func presentView(_ view: UIView) {
        view.alpha = 1
    }
    
    fileprivate func dismissView(_ view: UIView) {
        view.alpha = 0
    }
}

//
// Rendering
//

private class AADialogCellConfig {
    
    let item: ACDialog
    let titleWidth: CGFloat
    let isStatusVisible: Bool
    let contentWidth: CGFloat
    let typingText:String
    
    init(item: ACDialog, isStatusVisible: Bool, titleWidth: CGFloat, contentWidth: CGFloat, typingText:String) {
        self.item = item
        self.titleWidth = titleWidth
        self.contentWidth = contentWidth
        self.isStatusVisible = isStatusVisible
        self.typingText = typingText
    }
}

extension AADialogCellConfig: Equatable {
    
}

private func ==(lhs: AADialogCellConfig, rhs: AADialogCellConfig) -> Bool {
    return rhs.titleWidth == lhs.titleWidth && rhs.contentWidth == lhs.contentWidth
}

private class AADialogCellLayout {
    
    let titleLayout: YYTextLayout
    let counterLayout: YYTextLayout?
    let messageLayout: YYTextLayout
    let typingLayout: YYTextLayout
    let messageWidth: CGFloat
    let dateLayout: YYTextLayout
    
    init(
        titleLayout: YYTextLayout,
        messageLayout: YYTextLayout,
        typingLayout: YYTextLayout,
        messageWidth: CGFloat,
        counterLayout: YYTextLayout?,
        dateLayout: YYTextLayout) {
            self.titleLayout = titleLayout
            self.counterLayout = counterLayout
            self.messageLayout = messageLayout
            self.typingLayout = typingLayout
            self.messageWidth = messageWidth
            self.dateLayout = dateLayout
    }
}



