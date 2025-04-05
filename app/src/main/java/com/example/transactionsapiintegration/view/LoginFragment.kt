package com.example.transactionsapiintegration.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.transactionsapiintegration.utils.SharedPref
import com.dictatenow.androidapp.utils.Status
import com.dictatenow.androidapp.utils.makeToast
import com.example.transactionsapiintegration.R
import com.example.transactionsapiintegration.databinding.FragmentLoginBinding
import com.example.transactionsapiintegration.utils.Constants
import com.example.transactionsapiintegration.view.viewModel.LoginViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()
    private val pref: SharedPref by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener(this@LoginFragment)
        }
        observeViewModel()
        backPressed()

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnLogin -> {
                hitAPIToLogin()
            }
        }
    }

    private fun hitAPIToLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        when {
            username.isEmpty() -> {
                requireActivity().makeToast("Enter Username")
            }
            password.isEmpty() -> {
                requireActivity().makeToast("Enter Password")
            }
            else -> {
                val jsonObject = JsonObject()
                jsonObject.addProperty("username", username)
                jsonObject.addProperty("password", password)
                CoroutineScope(Dispatchers.Main).launch {
                    loginViewModel.login(jsonObject)
                }
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        loginViewModel.loginData.observe(this@LoginFragment) {
            when (it.status) {
                Status.SUCCESS -> {
                    val data = it.data as JsonObject
                    val token = data.get("token").toString().replace("\"", "")
                    pref.saveString(Constants.TOKEN, token)
                    val arg = Bundle()
                    arg.putBoolean("FromLogin", true)
                    findNavController().navigate(
                        R.id.action_loginFragment_to_transactionsFragment, arg
                    )
                }

                Status.ERROR -> {
                    requireContext().makeToast(it.message!!)
                }

                Status.LOADING -> {

                }
            }
        }
    }

    private fun backPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}