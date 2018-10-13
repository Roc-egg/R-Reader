package hp.redreader.com.app.utils

import hp.redreader.com.mvp.model.entity.movie.PersonBean

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/24/024 1:13
 * 修改人：  hp
 * 修改时间：2018/9/24/024 1:13
 * 修改备注：
 */
object StringFormatUtil {
    /**
     * 格式化导演、主演名字
     */
    fun formatName(casts: List<PersonBean>?): String {
        return if (casts != null && casts.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            for (i in casts.indices) {
                if (i < casts.size - 1) {
                    stringBuilder.append(casts[i].getName()).append(" / ")
                } else {
                    stringBuilder.append(casts[i].getName())
                }
            }
            stringBuilder.toString()

        } else {
            "佚名"
        }
    }

    /**
     * 格式化电影类型
     */
    fun formatGenres(genres: List<String>?): String {
        return if (genres != null && genres.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            for (i in genres.indices) {
                if (i < genres.size - 1) {
                    stringBuilder.append(genres[i]).append(" / ")
                } else {
                    stringBuilder.append(genres[i])
                }
            }
            stringBuilder.toString()

        } else {
            "不知名类型"
        }
    }
}