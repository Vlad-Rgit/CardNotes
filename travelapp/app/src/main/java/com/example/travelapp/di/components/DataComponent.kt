package com.example.travelapp.di.components

import com.example.travelapp.di.modules.InteractorsModule
import com.example.travelapp.di.modules.RepoModule
import com.example.travelapp.di.modules.RetrofitModule
import com.example.travelapp.presentation.mainscreen.viewmodels.MainFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RetrofitModule::class,
    RepoModule::class,
    InteractorsModule::class])
@Singleton
interface DataComponent {
    fun inject(item: MainFragmentViewModel)
}