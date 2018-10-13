package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.OneModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.OneFragment

@FragmentScope
@Component(modules = arrayOf(OneModule::class), dependencies = arrayOf(AppComponent::class))
interface OneComponent {
    fun inject(fragment: OneFragment)
}
