package com.moonring.ring.bean

data class OtaUpdateState(val step: Int, val progress: Int) {
    companion object {
        const val STEP_DOWNLOAD_FILE = 1
        const val STEP_ENTRY_OTA = 2
        const val STEP_CONNECT_DEVICE = 3
        const val STEP_OTA = 4
        const val STEP_FINISH = 5

        const val PROGRESS_START = 0
        const val PROGRESS_WAIT = 50
        const val PROGRESS_END = 100
        const val PROGRESS_MD5_ERR = 101
    }
}