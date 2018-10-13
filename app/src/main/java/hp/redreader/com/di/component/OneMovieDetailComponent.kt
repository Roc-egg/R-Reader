package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.OneMovieDetailModule

import com.jess.arms.di.scope.ActivityScope
import hp.redreader.com.mvp.ui.activity.movie.OneMovieDetailActivity

@ActivityScope
@Component(modules = arrayOf(OneMovieDetailModule::class), dependencies = arrayOf(AppComponent::class))
interface OneMovieDetailComponent {
    fun inject(activity: OneMovieDetailActivity)
}
