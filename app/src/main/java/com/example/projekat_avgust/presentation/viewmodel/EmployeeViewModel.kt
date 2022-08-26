package com.example.projekat_avgust.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekat_avgust.data.models.Employee
import com.example.projekat_avgust.data.models.Resource
import com.example.projekat_avgust.data.repositories.EmployeeRepository
import com.example.projekat_avgust.presentation.contract.EmployeeContract
import com.example.projekat_avgust.presentation.view.states.EmployeeState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class EmployeeViewModel  (private val employeeRepository: EmployeeRepository ) : ViewModel(), EmployeeContract.ViewModel {

    private val subscriptions = CompositeDisposable()
    override val employeeState: MutableLiveData<EmployeeState> = MutableLiveData()
    override var testVar: MutableLiveData<Int> = MutableLiveData()

    override fun fetchAllEmployeesFromServer() {
        val subscription = employeeRepository
            .fetchAllFromServer()
            .startWith(Resource.Loading())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    when(it) {
                        is Resource.Loading -> employeeState.value = EmployeeState.Loading
                        is Resource.Success -> employeeState.value = EmployeeState.DataFetched
                        is Resource.Error -> employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    }
                },
                {
                    employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    override fun getAllEmployees() {
        val subscription = employeeRepository
            .getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    employeeState.value = EmployeeState.Success(it)
                },
                {
                    employeeState.value = EmployeeState.Error("Error happened while fetching data from db")
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    override fun deleteEmployee(employeeId: Long) {
        val subscription = employeeRepository
            .delete(employeeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    when(it) {
                        is Resource.Success -> deleteFromDb(employeeId)
                        is Resource.Error -> employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    }
                },
                {
                    employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    private fun deleteFromDb(employeeId: Long){
        val subscription = employeeRepository
            .deleteById(employeeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.e("DELETED")
                },
                {
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    private fun updateFromDb(employeeId: Long, name: String, salary: Int, age: Int){
        val subscription = employeeRepository
            .updateById(employeeId, name, salary, age)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.e("UPDATED")
                },
                {
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    override fun updateEmployee(employeeId: Long, name: String, salary: Int, age: Int) {
        val subscription = employeeRepository
            .update(employeeId, name, salary, age)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    employeeState.value = EmployeeState.Updated(it)
                    updateFromDb(employeeId, name, salary, age)
                },
                {
                    employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    override fun detailedEmployee(employeeId: Long) {
        val subscription = employeeRepository
            .details(employeeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    employeeState.value = EmployeeState.Detailed(it)//todo stavi da detailed vuce iz base a ne sa servera
                },
                {
                    employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    override fun addNewEmployee(employee: Employee) {
        val subscription = employeeRepository
            .add(employee)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    employeeState.value = EmployeeState.Created(it)
                    addToDb(employee.id, employee.name, employee.salary.toInt(), employee.age.toInt())
                },
                {
                    employeeState.value = EmployeeState.Error("Error happened while fetching data from the server")
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }

    private fun addToDb(id: Long, name: String, salary: Int, age: Int){
        val subscription = employeeRepository
            .addToDb(id, name, salary, age)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.e("CREATED")
                },
                {
                    Timber.e(it)
                }
            )
        subscriptions.add(subscription)
    }


}