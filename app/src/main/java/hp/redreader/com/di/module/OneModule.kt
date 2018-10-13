package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.OneContract
import hp.redreader.com.mvp.model.OneModel
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.ui.adapter.OneAdapter


@Module
//构建OneModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class OneModule(private val view: OneContract.View) {
    @FragmentScope
    @Provides
    fun provideOneView(): OneContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideOneModel(model: OneModel): OneContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<SubjectsBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<SubjectsBean>) = OneAdapter(datas)
}
