package hp.redreader.com.app.utils.listener

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/19/019 23:01
 * 修改人：  hp
 * 修改时间：2018/9/19/019 23:01
 * 修改备注：
 */
interface WanNavigator {

    /**
     * 收藏或取消收藏
     */
    interface OnCollectNavigator {
        fun onSuccess()

        fun onFailure()
    }
}