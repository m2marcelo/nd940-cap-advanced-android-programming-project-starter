package com.example.android.politicalpreparedness.launch
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    private  lateinit var binding: FragmentLaunchBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentLaunchBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.representativesButton.setOnClickListener { navToRepresentatives() }
        binding.upcomingElectionsButton.setOnClickListener { navToElections()

        }
        return binding.root
    }

    private fun navToElections() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}