package se.onemanstudio.randy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import se.onemanstudio.randy.R
import se.onemanstudio.randy.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.surfaceView.initializeTheScene(requireActivity() as AppCompatActivity)

        binding.playPauseFab.setOnClickListener {
            binding.surfaceView.togglePlayback()

            if(binding.surfaceView.paused) {
                binding.playPauseFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play))
            } else {
                binding.playPauseFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause))
            }
        }

        binding.ballsPlay.setOnClickListener {
            binding.surfaceView.pause()
            binding.surfaceView.addJoints()
            binding.surfaceView.addDestinationForAll()
            binding.surfaceView.resume()
        }

        binding.ballsCustomise.setOnClickListener {
            val bottomSheet = BottomSheetDialogForBalls()
            bottomSheet.show(requireActivity().supportFragmentManager, "ModalBottomSheet")
        }
    }

    override fun onResume() {
        super.onResume()
        binding.surfaceView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.surfaceView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}