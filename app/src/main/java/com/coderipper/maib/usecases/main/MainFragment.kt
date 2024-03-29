package com.coderipper.maib.usecases.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.coderipper.maib.MainNavGraphDirections
import com.coderipper.maib.R
import com.coderipper.maib.databinding.FragmentMainBinding
import com.coderipper.maib.models.session.User
import com.coderipper.maib.usecases.login.LoginFragmentDirections
import com.coderipper.maib.usecases.main.stories.StoryOptions
import com.coderipper.maib.usecases.modals.createAvatarsModal
import com.coderipper.maib.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private lateinit var uname: String
    private lateinit var headerImage: AppCompatImageView
    private lateinit var avatarsDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = getStringValue(requireActivity(), "id")!!
        uname = getStringValue(requireActivity(), "uname") ?: ""
        val imgId = getIntValue(requireActivity(), "avatar")

        val channel = NotificationChannel("channel1", "Notf channel", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "description"
        val notificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(requireContext(), "channel1")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Hey! Volviste")
            .setContentText("Crea mas ahora!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, builder.build())
        }

        binding.run {
            val header = menuNavigation.getHeaderView(0)

            avatarSelected.setImageResource(imgId)
            sectionText.text = "¡Hola de nuevo $uname!"
            headerImage = header.findViewById(R.id.avatar_selected)
            headerImage.setImageResource(imgId)
            header.findViewById<MaterialTextView>(R.id.user_name_text).text = getStringValue(requireActivity(), "name")

            db.collection("users").document(userId).get().addOnSuccessListener { data ->
                if (data.exists()) {
                    val user = data.toObject<User>()
                    if (user != null) {
                        header.findViewById<MaterialTextView>(R.id.following_text).text = user.followers.size.toString()
                        header.findViewById<MaterialTextView>(R.id.rate_text).text = user.rate.toString()
                    } else
                        Snackbar.make(root, "Error con cuenta", Snackbar.LENGTH_SHORT).show()
                }
            }

            avatarBtn.setOnClickListener {
                avatarsDialog = createAvatarsModal(requireContext(), ::selectedAvatar)
            }

            avatarBtn.setOnLongClickListener {
                storyOptions()
                true
            }

            homeToolbar.setNavigationOnClickListener {
                drawerLayout.open()
            }

            drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    homeLayout.translationX = slideOffset * menuNavigation.width
                }

                override fun onDrawerOpened(drawerView: View) {}
                override fun onDrawerClosed(drawerView: View) {}
                override fun onDrawerStateChanged(newState: Int) {}
            })

            menuNavigation.setNavigationItemSelectedListener { menuItem ->
                menuItem.isChecked = true
                drawerLayout.close()

                when (menuItem.itemId) {
                    R.id.home -> {
                        sectionText.text = "¡Hola de nuevo $uname!"
                        navMainFragment.findNavController().navigate(MainNavGraphDirections.toHome())
                    }
                    R.id.dashboard -> {
                        sectionText.text = "Tu información"
                        navMainFragment.findNavController().navigate(MainNavGraphDirections.toDashboard())
                    }
                    R.id.account -> {
                        sectionText.text = "Tu cuenta"
                        navMainFragment.findNavController().navigate(MainNavGraphDirections.toAccount())
                    }
                    R.id.settings -> {
                        sectionText.text = "Configuración"
                        navMainFragment.findNavController().navigate(MainNavGraphDirections.toSettings())
                    }
                    R.id.help -> {
                        sectionText.text = "Ayuda"
                        navMainFragment.findNavController().navigate(MainNavGraphDirections.toHelp())
                    }
                    R.id.about_us -> {
                        sectionText.text = "Sobre nosotros"
                        navMainFragment.findNavController().navigate(MainNavGraphDirections.toAbout())
                    }
                    R.id.logout -> {
                        logout(requireActivity())
                        FirebaseAuth.getInstance().signOut()
                        root.findNavController().navigate(MainFragmentDirections.toLogin())
                    }
                }

                true
            }
        }
    }

    private fun selectedAvatar(imgId: Int) {
        val userId = getStringValue(requireActivity(), "id")!!

        binding.avatarSelected.setImageResource(imgId)
        headerImage.setImageResource(imgId)
        setIntValue(requireActivity(), "avatar", imgId)
        db.collection("users").document(userId).update("avatar", imgId)
        avatarsDialog.dismiss()
    }

    private fun storyOptions() {
        val storyOptions = StoryOptions.newInstance()
        storyOptions.show(parentFragmentManager, "Options")
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}