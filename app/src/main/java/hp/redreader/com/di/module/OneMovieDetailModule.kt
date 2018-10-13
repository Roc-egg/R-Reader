package hp.redreader.com.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.OneMovieDetailContract
import hp.redreader.com.mvp.model.OneMovieDetailModel
import hp.redreader.com.mvp.model.entity.movie.PersonBean
import hp.redreader.com.mvp.ui.adapter.MovieDetailAdapter


@Module
//构建OneMovieDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class OneMovieDetailModule(private val view: OneMovieDetailContract.View) {
    @ActivityScope
    @Provides
    fun provideOneMovieDetailView(): OneMovieDetailContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideOneMovieDetailModel(model: OneMovieDetailModel): OneMovieDetailContract.Model {
        return model
    }

    @ActivityScope
    @Provides
    internal fun provideDatas() = mutableListOf<PersonBean>()

    @ActivityScope
    @Provides
    internal fun provideAdapter(datas: MutableList<PersonBean>) = MovieDetailAdapter(datas)
}
