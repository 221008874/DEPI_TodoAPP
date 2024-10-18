package com.abdoahmed.todoapp.recycular_view_Adapter
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecycularViewAdapter(private val arr: ArrayList<TodoDataEntity>,private val Flag:Boolean , private val navController: NavController,private val isDeleteEnabled:Boolean,private val isUpdateEnabled:Boolean) :
    RecyclerView.Adapter<RecycularViewAdapter.ViewHolder>(){
    lateinit var view :View

    private var currentlyExpandedView: View? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecycularViewAdapter.ViewHolder {

        if (Flag == true) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.blank_row_design, parent, false)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.res_view_row_design, parent, false)
        }




        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecycularViewAdapter.ViewHolder, position: Int) {
        val ItemsViewModel = arr[position]

        for (Item in arr){
            if (arr[position].isActivated==1){
                holder.alramAcivate.setBackgroundColor(Color.GREEN)
            }
            else{
                holder.alramAcivate.setBackgroundColor(Color.GRAY)
            }
        }

        // sets the image to the imageview from our itemHolder class
        holder.contentTextView.setText(ItemsViewModel.Content)

        // sets the text to the textview from our itemHolder class
        holder.titleTextView.setText(ItemsViewModel.Title)

        holder.startTime.setText(ItemsViewModel.startTime)

        holder.EndTime.setText(ItemsViewModel.EndTime)

       // holder.startDate.setText(ItemsViewModel.StartDate)

        //holder.EndDate.setText(ItemsViewModel.EndDate)

        holder.alramAcivate.setOnClickListener(){



            activateAlarm(position,holder.alramAcivate)

        }


        holder.completedButton.setOnClickListener(){
            updateCompleteStateInDpToCompleted(position)
            Toast.makeText(view.context, "Todo Completed (:", Toast.LENGTH_SHORT).show()
            holder.completedButton.setBackgroundColor(Color.RED)
            deleteFromResycalrView(position)

        }


        holder.DeleteButton.setOnClickListener() {

            if (isEnabledDeleteButton(isDeleteEnabled, view) == true) {
                itemCount
                deleteElementFromView(position)
                Log.i("delete", "delete Pressed")
            }
            else {

            }



        }


        val menue=holder.menue
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



        holder.ViewButton.setOnClickListener(){
            val action=MainBageDirections.actionMainBageToViewTodoOrUpdate(arr[position].id, updateState = isUpdateEnabled)
            navController.navigate(action, NavOptions.Builder()
                .setPopUpTo(R.id.viewTodoOrUpdate, true)
                .setEnterAnim(R.anim.to_right)  // Animation for entering the new fragment
                .setExitAnim(R.anim.to_left)   // Animation for exiting the current fragment
                .setPopEnterAnim(R.anim.from_left)  // Animation when popping back to the fragment
                .setPopExitAnim(R.anim.from_right)// This removes the current fragment from the back stack// This removes the current fragment from the back stack
                .build())
        }//Done

    }

    override fun getItemCount(): Int {
        return arr.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val contentTextView: TextView = itemView.findViewById(R.id.RowContent)

        val titleTextView:TextView=itemView.findViewById(R.id.RowTitle)

        val DeleteButton:Button=itemView.findViewById(R.id.DeleteButton)

        val ViewButton:Button=itemView.findViewById(R.id.View)

        val startTime:TextView=itemView.findViewById(R.id.strtTimeClock)

        val EndTime:TextView=itemView.findViewById(R.id.endTimeClock)

        val completedButton :Button=itemView.findViewById(R.id.Todocompleted)

        val DropMenueToggelButton=itemView.findViewById<Button>(R.id.DropMenueTogile)

        val alramAcivate=itemView.findViewById<Button>(R.id.ActivateAlarm)

        val menue=itemView.findViewById<ViewGroup>(R.id.dropMenue)
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
    private fun deleteFromResycalrView(position: Int){
        if (position in arr.indices) {
            val removedItem = arr[position]
            arr.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, arr.size)

        } else {


            Log.e("Error", "Invalid position for deletion: $position")
        }
    }

    private fun activateAlarm(position: Int,activate:Button) {
        // Ensure the position is valid
        if (position < 0 || position >= arr.size) {
            Log.e("AlarmActivation", "Invalid position: $position")
            return
        }

        // Get the selected item from the list
        val selectedItem = arr[position]
        val dp = __DatabaseBuilder__.getDatabaseInstance(view.context)
        val daoImp = implementation(dp)

        // CoroutineScope to ensure that the coroutine is lifecycle-aware
        CoroutineScope(Dispatchers.Main).launch {
            val check: TodoDataEntity? = withContext(Dispatchers.IO) {
                daoImp.__SelectTodoOnId(selectedItem.id)
            }

            if (check == null) {
                Toast.makeText(view.context, "Todo item not found", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (check.isActivated == 1) {
                withContext(Dispatchers.IO) {
                    daoImp.updateAlarmStatus(todoId = selectedItem.id, isActive = 0)
                }
                activate.setBackgroundColor(Color.GRAY)
                Toast.makeText(view.context, "Alarm deactivated for ${selectedItem.Title}", Toast.LENGTH_SHORT).show()
            } else {
                withContext(Dispatchers.IO) {
                    daoImp.updateAlarmStatus(todoId = selectedItem.id, isActive = 1)

                }

                // Log the details of the selected item for debugging
                Log.d("AlarmActivation", "Activating alarm for: ${selectedItem.Title} with ID: ${selectedItem.id}")

                // Create an instance of Alarm and set up the calendar operations
                val alarm = Alarm(view.context, selectedItem.id, navController, username = selectedItem.username.toString())

                // Call the method to create calendar operations, which will set the alarms
                alarm.createCalenderOp()
                activate.setBackgroundColor(Color.GREEN)
                // Provide user feedback (optional)
                Toast.makeText(view.context, "Alarm set for ${selectedItem.Title}", Toast.LENGTH_SHORT).show()
            }
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


    private fun isEnabledDeleteButton(isEnabled:Boolean,view: View):Boolean{

        var enabled:Boolean
        if (isEnabled==true){
            view.findViewById<Button>(R.id.DeleteButton).isEnabled=true
            Toast.makeText(view.context, "Warning delete enabled", Toast.LENGTH_LONG).show()
            enabled=true
            return enabled
        }
        else{
            view.findViewById<Button>(R.id.DeleteButton).isEnabled=false
            view.findViewById<Button>(R.id.DeleteButton).isEnabled=true
            Toast.makeText(view.context, "Delete Disabled from app settings", Toast.LENGTH_LONG).show()
            enabled=false
            return enabled
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
            duration = 500
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





    private fun updateCompleteStateInDpToCompleted(position: Int){
        val dp = __DatabaseBuilder__.getDatabaseInstance(view.context)
        val daoImp = implementation(dp)

        var selectedItem=arr[position]
        GlobalScope.launch (Dispatchers.IO){
            daoImp.__UpdateTodoCompleteState(id=selectedItem.id, state = 1)
        }

    }




    }