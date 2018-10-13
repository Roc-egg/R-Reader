package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.JokeModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.wanChild.JokeFragment

@FragmentScope
@Component(modules = arrayOf(JokeModule::class), dependencies = arrayOf(AppComponent::class))
interface JokeComponent {
    fun inject(fragment: JokeFragment)
}
