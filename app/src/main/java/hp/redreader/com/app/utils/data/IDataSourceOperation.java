package hp.redreader.com.app.utils.data;

import java.util.List;

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/20/020 20:06
 * 修改人：  hp
 * 修改时间：2018/9/20/020 20:06
 * 修改备注：
 */
public interface IDataSourceOperation {
    void insert(Object object);

    void insertOrUpdate(Object object);

    void update(Object object);

    void delete(Object object);

    void deleteByKey(Class<? extends Object> classType, String id);

    void deleteAll(Class<? extends Object> classType);

    <T> T quaryByKey(Class<T> classType, String id);

    <T> List<T> quary(Class<T> classType, String where, String... selectionArg);

    <T> List<T> quaryAll(Class<T> classType);

}
