package com.example.transactionsapiintegration.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dictatenow.androidapp.utils.Status
import com.dictatenow.androidapp.utils.makeToast
import com.example.transactionsapiintegration.R
import com.example.transactionsapiintegration.databinding.FragmentTransactionsBinding
import com.example.transactionsapiintegration.model.Transaction
import com.example.transactionsapiintegration.utils.Constants
import com.example.transactionsapiintegration.utils.SharedPref
import com.example.transactionsapiintegration.view.adapter.TransactionAdapter
import com.example.transactionsapiintegration.view.viewModel.TransactionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionsFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentTransactionsBinding
    private val transactionsViewModel: TransactionsViewModel by viewModel()
    private val pref: SharedPref by inject()

    private var transactionAdapter: TransactionAdapter ?=null

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments == null) {
            setupBiometricPrompt()
        } else {
            if (requireArguments().getBoolean("FromLogin")) {
                hitApiToGetTransaction()
            }
        }
        observeViewModel()
        backPressed()
        binding.ibLogout.setOnClickListener(this@TransactionsFragment)
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
               return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(transactionAdapter!=null){
                    transactionAdapter!!.filter(p0!!)
                }
                return true
            }

        })
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    hitApiToGetTransaction() // Only call API after auth success
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    requireActivity().finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    requireContext().makeToast("Authentication failed")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to view your transactions")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                requireContext().makeToast("No biometric hardware found")

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                requireContext().makeToast("Biometric hardware currently unavailable")

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                requireContext().makeToast("No biometric credentials enrolled")

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {}

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {}

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {}
        }
    }


    private fun hitApiToGetTransaction() {
        CoroutineScope(Dispatchers.Main).launch {
            transactionsViewModel.getTransaction()
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        transactionsViewModel.transactionData.observe(this@TransactionsFragment) {
            when (it.status) {
                Status.SUCCESS -> {
                    val data = it.data as List<Transaction>
                    setAdapter(data)
                }

                Status.ERROR -> {
                    requireContext().makeToast(it.message!!)
                }

                Status.LOADING -> {

                }
            }
        }
    }

    private fun setAdapter(transactionList: List<Transaction>) {
        transactionAdapter = TransactionAdapter(transactionList)
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ibLogout -> {
                requireActivity().runOnUiThread {
                    requireActivity().makeToast("Yes Clicked")
                    pref.saveString(Constants.TOKEN, "")

                    findNavController().navigate(
                        R.id.action_transactionsFragment_to_loginFragment
                    )
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