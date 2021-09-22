package ru.otus.popit.ui.main

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.gridlayout.widget.GridLayout
import ru.otus.popit.R
import ru.otus.popit.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val fd = requireContext().assets.openFd("pop.wav")
            viewModel.initSound(fd)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateBtn.setOnClickListener { viewModel.onUpdateClick() }

        viewModel.state.observe(viewLifecycleOwner) {
            binding.field.removeAllViews()
            it.forEachIndexed { index, state ->
                val row = index / 5
                val column = index % 5
                val params = GridLayout.LayoutParams(
                    GridLayout.spec(row, 1f),
                    GridLayout.spec(column, 1f)
                )

                val item = ImageView(requireContext()).apply {
                    setImageResource(R.drawable.ic_circle_selectable)
                    setBackgroundResource(R.drawable.background_item)
                    isEnabled = state
                    isHapticFeedbackEnabled = true
                    setOnClickListener { v ->
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        viewModel.onPopClick(index)
                    }
                }
                binding.field.addView(item, params)
            }
        }
        viewModel.cellStateByIndex.observe(viewLifecycleOwner) {
            val (index, state) = it
            binding.field.getChildAt(index).isEnabled = state
        }
        viewModel.timerState.observe(viewLifecycleOwner) {
            binding.timer.text = String.format("%.1f", it)
        }

    }
}