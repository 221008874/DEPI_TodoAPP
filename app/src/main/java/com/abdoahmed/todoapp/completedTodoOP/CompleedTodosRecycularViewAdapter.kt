import android.animation.ValueAnimator
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__
import com.abdoahmed.todoapp.MainBageDirections

import com.abdoahmed.todoapp.R
import com.abdoahmed.todoapp.TaskAlarm.Alarm.Alarm

import com.abdoahmed.todoapp.completedTodoOP.CompletedTodosDirections
import com.abdoahmed.todoapp.recycular_view_Adapter.RecycularViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CompleedTodosRecycularViewAdapter(
    private val arr: ArrayList<TodoDataEntity>,
    private val Flag: Boolean,
    private val navController: NavController,
    private val isDeleteEnabled: Boolean,
    private val isUpdateEnabled: Boolean
) : RecyclerView.Adapter<CompleedTodosRecycularViewAdapter.ViewHolder>() { // Updated line
    private var currentlyExpandedView: View? = null
    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // Updated line
        if (Flag) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.blank_row_design, parent, false)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.completedtodorowdesign, parent, false)
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { // Updated line
        val ItemsViewModel = arr[position]

        // sets the image to the imageview from our itemHolder class
        holder.contentTextView.setText(ItemsViewModel.Content)

        // sets the text to the textview from our itemHolder class
        holder.titleTextView.setText(ItemsViewModel.Title)

        holder.startTime.setText(ItemsViewModel.startTime)
        holder.EndTime.setText(ItemsViewModel.EndTime)



        holder.completedButton.setOnClickListener {
            updateCompleteStateInDpToCompleted(position)
            Toast.makeText(view.context, "Todo not completed ):", Toast.LENGTH_SHORT).show()
            holder.completedButton.setBackgroundColor(Color.RED)
            deleteFromResycalrView(position)
        }

        holder.DeleteButton.setOnClickListener {
            if (isEnabledDeleteButton(isDeleteEnabled, view)) {
                itemCount
                deleteElementFromView(position)
                Log.i("delete", "delete Pressed")
            }
        }

        val menue = holder.menue
        holder.DropMenueToggelButton.setOnClickListener {
            if (currentlyExpandedView != null) {
                if (currentlyExpandedView == menue) {
                    // If the same view is clicked, collapse it
                    currentlyExpandedView?.animateExpandOrCollapse(expand = false)
                    currentlyExpandedView = null
                } else {
                    // Collapse the previously expanded view
                    currentlyExpandedView?.animateExpandOrCollapse(expand = false)
                    // After collapsing, expand the new view
                    menue.postDelayed({
                        menue.animateExpandOrCollapse(expand = true)
                        currentlyExpandedView = menue
                    }, 300) // Match this delay with your animation duration
                }
            } else {
                // No view is expanded, so just expand the new view
                menue.animateExpandOrCollapse(expand = true)
                currentlyExpandedView = menue
            }
        }

        holder.ViewButton.setOnClickListener {
            val action =
               CompletedTodosDirections.actionCompletedTodosToViewCompletedTodos(arr[position].id, updateState = isUpdateEnabled)
            navController.navigate(action, NavOptions.Builder()
                .setPopUpTo(R.id.viewTodoOrUpdate, true)
                .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                .setPopExitAnim(R.anim.from_right) // This removes the current fragment from the back stack
                .build())
        } //Done
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.RowContent)
        val titleTextView: TextView = itemView.findViewById(R.id.RowTitle)
        val DeleteButton: Button = itemView.findViewById(R.id.DeleteButton)
        val ViewButton: Button = itemView.findViewById(R.id.View)
        val startTime: TextView = itemView.findViewById(R.id.strtTimeClock)
        val EndTime: TextView = itemView.findViewById(R.id.endTimeClock)
        val completedButton: Button = itemView.findViewById(R.id.Todocompleted)
        val DropMenueToggelButton = itemView.findViewById<Button>(R.id.DropMenueTogile)

        val menue = itemView.findViewById<ViewGroup>(R.id.dropMenue)
    }

    private fun deleteElementFromView(position: Int) {
        if (position in arr.indices) {
            val removedItem = arr[position]
            arr.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, arr.size)
            deleteElementFromDatabase(removedItem.id)
        } else {
            Log.e("Error", "Invalid position for deletion: $position")
        }
    }

    private fun deleteFromResycalrView(position: Int) {
        if (position in arr.indices) {
            val removedItem = arr[position]
            arr.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, arr.size)
        } else {
            Log.e("Error", "Invalid position for deletion: $position")
        }
    }



    private fun deleteElementFromDatabase(id: Int) {
        val dp = __DatabaseBuilder__.getDatabaseInstance(view.context)
        val daoImp = implementation(dp)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                daoImp.__DeleteTodo(id)
                Log.i("Delete Todo", "Todo with id of: $id deleted")
            } catch (e: Exception) {
                Log.e("Database Error", "Failed to delete Todo with id: $id, ${e.message}")
            }
        }
    }

    private fun isEnabledDeleteButton(isEnabled: Boolean, view: View): Boolean {
        return if (isEnabled) {
            view.findViewById<Button>(R.id.DeleteButton).isEnabled = true
            Toast.makeText(view.context, "Warning delete enabled", Toast.LENGTH_LONG).show()
            true
        } else {
            view.findViewById<Button>(R.id.DeleteButton).isEnabled = false
            Toast.makeText(view.context, "Delete Disabled from app settings", Toast.LENGTH_LONG).show()
            false
        }
    }

    private var isAnimating = false
    fun View.animateExpandOrCollapse(expand: Boolean) {
        // Check if an animation is already in progress
        if (isAnimating) {
            return
        }

        // Measure the target height
        val targetHeight = if (expand) measureAndGetHeight() else 0
        val initialHeight = if (expand) 0 else measuredHeight

        if (visibility == View.GONE && expand) {
            visibility = View.VISIBLE
            layoutParams.height = 0
            requestLayout()
        }

        // Only animate if there is a height change
        if (initialHeight == targetHeight) return

        // Set animation flag to prevent overlapping animations
        isAnimating = true

        // Animate height change
        val animator = ValueAnimator.ofInt(initialHeight, targetHeight).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
        }

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            layoutParams.height = animatedValue
            requestLayout()
        }

        animator.addListener(onEnd = {
            // Ensure view height is set correctly after animation
            if (!expand) {
                visibility = View.GONE
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            isAnimating = false // Reset the animation flag
            requestLayout()
        })

        animator.start()
    }

    private fun View.measureAndGetHeight(): Int {
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return measuredHeight
    }

    private fun updateCompleteStateInDpToCompleted(position: Int) {
        val dp = __DatabaseBuilder__.getDatabaseInstance(view.context)
        val daoImp = implementation(dp)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                daoImp.__UpdateTodoCompleteState(arr[position].id,0)
                Log.i("Update Todo", "Todo with id of: ${arr[position].id} updated to completed")
            } catch (e: Exception) {
                Log.e("Database Error", "Failed to update Todo with id: ${arr[position].id}, ${e.message}")
            }
        }
    }
}
