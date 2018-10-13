package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.WelfareContract
import hp.redreader.com.mvp.model.WelfareModel
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.ui.adapter.WelfareAdapter


@Module
//构建WelfareModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class WelfareModule(private val view: WelfareContract.View) {
    @FragmentScope
    @Provides
    fun provideWelfareView(): WelfareContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideWelfareModel(model: WelfareModel): WelfareContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<GankIoDataBean.ResultBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<GankIoDataBean.ResultBean>) = WelfareAdapter(datas)
}
