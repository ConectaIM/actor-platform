//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

public extension NSObject {
    func log(_ text:String) {
        if ActorSDK.isDebugMode{
            NSLog(text)
        }
    }
}

