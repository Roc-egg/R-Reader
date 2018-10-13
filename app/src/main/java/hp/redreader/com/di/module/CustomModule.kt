package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.CustomContract
import hp.redreader.com.mvp.model.CustomModel
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.ui.adapter.GankAndroidAdapter


@Module
//构建CustomModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CustomModule(private val view: CustomContract.View) {
    @FragmentScope
    @Provides
    fun provideCustomView(): CustomContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideCustomModel(model: CustomModel): CustomContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<GankIoDataBean.ResultBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<GankIoDataBean.ResultBean>) = GankAndroidAdapter(datas)
}
