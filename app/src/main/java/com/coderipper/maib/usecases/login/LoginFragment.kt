package com.coderipper.maib.usecases.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.coderipper.maib.databinding.FragmentLoginBinding
import com.coderipper.maib.utils.*
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            if(!getStringValue(requireActivity(), "email").isNullOrEmpty())
                root.findNavController().navigate(LoginFragmentDirections.toHome())

            signInText.setOnClickListener {
                root.findNavController().navigate(LoginFragmentDirections.toSignIn())
            }

            loginFab.setOnClickListener {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        binding.run {
            val email = emailInput.text.toString().trim()
            val pswd = passwordInput.text.toString().trim()

            if(email.isNotEmpty() && pswd.isNotEmpty()) {
                val user = DataBase.loginUser(email, pswd)
                if(user != null) {
                    setLongValue(requireActivity(), "id", user.id)
                    setStringValue(requireActivity(), "email", user.email)
                    setStringValue(requireActivity(), "uname", user.name)
                    setStringValue(requireActivity(), "name", "${user.name} ${user.last}")
                    setIntValue(requireActivity(), "avatar", user.avatar)
                    root.findNavController().navigate(LoginFragmentDirections.toHome())
                }else
                    Snackbar.make(root, "Usuario no registrado", Snackbar.LENGTH_SHORT)
                        .setAction("??Unete!") {
                            root.findNavController().navigate(LoginFragmentDirections.toSignIn())
                        }.show()
            } else
                Snackbar.make(root, "Campos vacios", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}