package com.example.projekat_avgust.presentation.view.states

import com.example.projekat_avgust.data.models.Employee

sealed class
EmployeeState {
    object Loading: EmployeeState()
    object DataFetched: EmployeeState()
    data class Success(val employees: List<Employee>): EmployeeState()
    data class Error(val message: String): EmployeeState()
}