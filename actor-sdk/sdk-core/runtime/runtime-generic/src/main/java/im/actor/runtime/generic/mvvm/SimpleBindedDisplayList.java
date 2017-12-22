/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.runtime.generic.mvvm;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;
import java.util.List;

import im.actor.runtime.Log;
import im.actor.runtime.annotations.MainThread;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.generic.mvvm.alg.Modification;
import im.actor.runtime.generic.mvvm.alg.Modifications;
import im.actor.runtime.mvvm.ValueModel;
import im.actor.runtime.storage.ListEngineDisplayExt;
import im.actor.runtime.storage.ListEngineDisplayListener;
import im.actor.runtime.storage.ListEngineDisplayLoadCallback;
import im.actor.runtime.storage.ListEngineItem;

// Disabling Bounds checks for speeding up calculations

/*-[
#define J2OBJC_DISABLE_ARRAY_BOUND_CHECKS 1
]-*/

public class SimpleBindedDisplayList<T extends BserObject & ListEngineItem>{

    private static final String TAG = "SimpleBindedDisplayList";

    private final ListEngineDisplayExt<T> listEngine;
    private final ListEngineDisplayListener<T> engineListener;
    private List<T> currentList;
    private Filter<T> filter;


    public SimpleBindedDisplayList(ListEngineDisplayExt<T> listEngine){
        this.listEngine = listEngine;

        engineListener = new ListEngineDisplayListener<T>() {
            @Override
            public void onItemRemoved(long key) {
                Log.d(TAG, "onItemRemoved");
            }

            @Override
            public void onItemsRemoved(long[] keys) {
                Log.d(TAG, "onItemsRemoved");
            }

            @Override
            public void addOrUpdate(T item) {
                Log.d(TAG, "addOrUpdate");
            }

            @Override
            public void addOrUpdate(List<T> items) {
                Log.d(TAG, "addOrUpdate");
            }

            @Override
            public void onItemsReplaced(List<T> items) {
                Log.d(TAG, "onItemsReplaced");
            }

            @Override
            public void onListClear() {
                Log.d(TAG, "onListClear");
            }
        };
        listEngine.subscribe(engineListener);

        fillCurrentList(listEngine);
    }

    private void fillCurrentList(ListEngineDisplayExt<T> listEngine){
        if(currentList == null){
            currentList = new ArrayList<>();
        }

        currentList.clear();

//        for(int i = 0; i < listEngine.getCount(); i++){
//            listEngine.
//
//            if(filter != null){
//
//            }else{
//                //currentList.add(listEngine.)
//            }
//        }

    }

    @MainThread
    @ObjectiveCName("dispose")
    public void dispose() {
        im.actor.runtime.Runtime.checkMainThread();
        listEngine.unsubscribe(engineListener);
    }


    @MainThread
    @ObjectiveCName("resume")
    public void resume() {
        im.actor.runtime.Runtime.checkMainThread();
        listEngine.subscribe(engineListener);
    }

    public static interface Filter<T>{
        boolean accept(T value);
    }

}