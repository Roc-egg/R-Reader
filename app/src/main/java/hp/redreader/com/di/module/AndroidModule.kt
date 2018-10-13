package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.AndroidContract
import hp.redreader.com.mvp.model.AndroidModel
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.ui.adapter.GankAndroidAdapter


@Module
//构建AndroidModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class AndroidModule(private val view: AndroidContract.View) {
    @FragmentScope
    @Provides
    fun provideAndroidView(): AndroidContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideAndroidModel(model: AndroidModel): AndroidContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<GankIoDataBean.ResultBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<GankIoDataBean.ResultBean>) = GankAndroidAdapter(datas)
}
