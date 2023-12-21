package se.onemanstudio.randy.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import se.onemanstudio.randy.R
import se.onemanstudio.randy.databinding.BottomSheetLayoutBinding
import se.onemanstudio.randy.views.BallsConfig


class BottomSheetDialogForBalls : BottomSheetDialogFragment() {
    private var _binding: BottomSheetLayoutBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.randomAmountOfJoints.setOnCheckedChangeListener { _, isChecked ->
            BallsConfig.addRandomAmountOfJoints = isChecked
        }

        binding.hasRandomSpeed.setOnCheckedChangeListener { _, isChecked ->
            BallsConfig.jointsHaveRandomSpeed = isChecked
        }

        binding.connectionsAnimate.setOnCheckedChangeListener { _, isChecked ->
            BallsConfig.connectionsAnimate = isChecked
        }

        binding.jointsHaveRandomColor.setOnCheckedChangeListener { _, isChecked ->
            BallsConfig.jointsHaveRandomColor = isChecked
        }

        binding.discreteRangeSlider.addOnChangeListener { _, value, _ ->
            BallsConfig.distanceToDrawConnection = value
        }

        val items = listOf("Simple lines", "Curves", "Bezier")
        val stylesAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.lineStyleOptions.setAdapter(stylesAdapter)
        (binding.lineStyleOptions as AutoCompleteTextView).setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> BallsConfig.connectionStyle = BallsConfig.ConnectionStyle.SIMPLE
                1 -> BallsConfig.connectionStyle = BallsConfig.ConnectionStyle.CURVES
                2 -> BallsConfig.connectionStyle = BallsConfig.ConnectionStyle.BEZIER
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Toast.makeText(requireContext(), "CANCEL", Toast.LENGTH_SHORT).show()
    }
}
