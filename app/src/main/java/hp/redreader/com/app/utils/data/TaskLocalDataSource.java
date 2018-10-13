package hp.redreader.com.app.utils.data;

import android.content.Context;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.util.List;

import hp.redreader.com.greendao.DaoMaster;
import hp.redreader.com.greendao.DaoSession;

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/20/020 20:07
 * 修改人：  hp
 * 修改时间：2018/9/20/020 20:07
 * 修改备注：
 */
public class TaskLocalDataSource implements IDataSourceOperation {
    private static final String DATABASE_NAME = "hp.db";//数据库名称
    private volatile static TaskLocalDataSource mDaoManager;//多线程访问
    DaoSession mDaoSession;
    DatabaseOpenHelper mHelper;

    /**
     * 使用单例模式获得操作数据库的对象
     *
     * @return
     */
    public static TaskLocalDataSource getInstance(Context context) {
        TaskLocalDataSource instance = null;
        if (mDaoManager == null) {
            synchronized (TaskLocalDataSource.class) {
                if (instance == null) {
                    instance = new TaskLocalDataSource(context);
                    mDaoManager = instance;
                }
            }
        }
        return mDaoManager;
    }


    public TaskLocalDataSource(Context context) {
        if (null == mDaoSession) {
            mHelper = new DatabaseOpenHelper(context);
            DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDb());
            mDaoSession = daoMaster.newSession(IdentityScopeType.None);
        }
    }

    public <T> T getDao(Class<? extends Object> entityClass) {
        return (T) mDaoSession.getDao(entityClass);
    }

    @Override
    public void insert(Object object) {
        Class cls;
        if (object instanceof List) {
            List listObject = (List) object;
            if (listObject.isEmpty()) {
                throw new IllegalArgumentException("List Object Not Allow Empty!");
            }
            cls = listObject.get(0).getClass();
            ((AbstractDao<Object, String>) getDao(cls)).insertInTx(listObject);
        } else {
            cls = object.getClass();
            ((AbstractDao<Object, String>) getDao(cls)).insert(object);
        }
    }

    @Override
    public void insertOrUpdate(Object object) {
        Class cls;
        if (object instanceof List) {
            List listObject = (List) object;
            if (listObject.isEmpty()) {
                throw new IllegalArgumentException("List Object Not Allow Empty!");
            }
            cls = listObject.get(0).getClass();
            ((AbstractDao<Object, String>) getDao(cls)).insertOrReplaceInTx(listObject);
        } else {
            cls = object.getClass();
            ((AbstractDao<Object, String>) getDao(cls)).insertOrReplace(object);
        }
    }

    @Override
    public void update(Object object) {
        Class cls;
        if (object instanceof List) {
            List listObject = (List) object;
            if (listObject.isEmpty()) {
                throw new IllegalArgumentException("List Object Not Allow Empty!");
            }
            cls = listObject.get(0).getClass();
            ((AbstractDao<Object, String>) getDao(cls)).updateInTx(listObject);
        } else {
            cls = object.getClass();
            ((AbstractDao<Object, String>) getDao(cls)).update(object);
        }
    }

    @Override
    public void delete(Object object) {
        Class cls;
        if (object instanceof List) {
            List listObject = (List) object;
            if (listObject.isEmpty()) {
                throw new IllegalArgumentException("List Object Not Allow Empty!");
            }
            cls = listObject.get(0).getClass();
            ((AbstractDao<Object, String>) getDao(cls)).deleteInTx(listObject);
        } else {
            cls = object.getClass();
            ((AbstractDao<Object, String>) getDao(cls)).delete(object);
        }
    }

    @Override
    public void deleteByKey(Class<?> cls, String key) {
        ((AbstractDao<Object, String>) getDao(cls)).deleteByKey(key);
    }

    @Override
    public void deleteAll(Class<?> cls) {
        ((AbstractDao<Object, String>) getDao(cls)).deleteAll();
    }

    @Override
    public <T> T quaryByKey(Class<T> cls, String key) {
        return ((AbstractDao<T, String>) getDao(cls)).load(key);
    }

    @Override
    public <T> List<T> quary(Class<T> cls, String where, String... selectionArg) {
        return ((AbstractDao<T, String>) getDao(cls)).queryRaw(where, selectionArg);
    }

    @Override
    public <T> List<T> quaryAll(Class<T> cls) {
        return ((AbstractDao<T, String>) getDao(cls)).loadAll();
    }


    static class DatabaseOpenHelper extends DaoMaster.OpenHelper {

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME);
        }
    }
}
