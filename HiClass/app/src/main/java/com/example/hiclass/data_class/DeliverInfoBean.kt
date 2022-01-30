package com.example.hiclass.data_class

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliverInfoBean(
    var alarmId: Long,
    var queInfo: ResourceBean
) : Parcelable {
}