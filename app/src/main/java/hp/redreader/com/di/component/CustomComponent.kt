package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.CustomModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.gankChild.CustomFragment

@FragmentScope
@Component(modules = arrayOf(CustomModule::class), dependencies = arrayOf(AppComponent::class))
interface CustomComponent {
    fun inject(fragment: CustomFragment)
}
