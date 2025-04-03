package com.moonring.ring.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.moonring.ring.R
import com.moonring.ring.bean.BleDeviceItem
import com.moonring.ring.databinding.ItemMoonringBleBinding

/**
 *    author : Administrator

 *    time   : 2024/3/8/008
 *    desc   :
 */
class BleMoonringAdapter :
    BaseQuickAdapter<BleDeviceItem, BaseDataBindingHolder<ItemMoonringBleBinding>>(R.layout.item_moonring_ble) {

    override fun convert(holder: BaseDataBindingHolder<ItemMoonringBleBinding>, item: BleDeviceItem) {
        holder.dataBinding?.apply {
            scan = item
            executePendingBindings()
        }
    }
}
