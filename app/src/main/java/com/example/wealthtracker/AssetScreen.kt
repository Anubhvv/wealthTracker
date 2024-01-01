package com.example.wealthtracker

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetScreen(context: Context) {
    var wealthData by remember { mutableStateOf(loadWealthData(context = context)) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryValue by remember { mutableStateOf(0.0) } // Initialize to 0.0

    var newCategoryValueText by remember { mutableStateOf("") } // Store value as text input

    // Create a boolean state to track if the dialog is open
    var isDialogOpen by remember { mutableStateOf(false) }
    // Create local state variables for the dialog inputs
    var categoryName by remember { mutableStateOf("") }
    var categoryValue by remember { mutableStateOf(0.0) }


    // Create a list of categories
    var categories by remember { mutableStateOf(wealthData.categories.toMutableList()) }
    val onValueChange: (String, Double) -> Unit = { categoryName, updatedValue ->
        // Find the category by name and update its value
        val categoryToUpdate = categories.find { it.name == categoryName }
        categoryToUpdate?.value = updatedValue

        // Update wealth data and save to local storage
        wealthData.categories = categories.toList()
        saveWealthData(context, wealthData)
        categories = loadWealthData(context = context).categories.toMutableList()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = "Assets",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(1.dp)
                .wrapContentHeight()
        )

        // Input for adding a new category
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {

        // Display existing categories
        LazyColumn {
            items(categories) { category ->
                Log.e("AssetScreen", "Category: ${category.name}")
                CategoryInput(
                    categoryName = category.name,
                    value = category.value,
                    existingValue = category.value,
                    context = context,
                    onValueChange = onValueChange,
                    onAddClick = { newValue, newName->
                        val newCategory = CategoryData(newName, newValue)
                        categories.add(newCategory)
                        wealthData.categories = categories.toList()

                        // Update SharedPreferences with newCategoryValue
                        val sharedPreferences = context.getSharedPreferences(WEALTH_PREFS_KEY, Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putFloat(newName, newValue.toFloat()) // Use newName and newValue here
                        editor.apply()

                        // Save updated wealth data to local storage
                        saveWealthData(context, wealthData)

                        // Clear the input fields
                        newCategoryName = ""
                        newCategoryValue = 0.0
                    }
                )
            }
        }
        // Button to add a new category

        // Dialog for entering category name and value
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    // Close the dialog
                    isDialogOpen = false
                },
                title = {
                    Text(text = "Add Category")
                },
                text = {
                    Column {
                        TextField(
                            value = newCategoryName,
                            placeholder = {Text("Enter a new category name like Gold, Crypto") },
                            onValueChange = { newValue ->
                                newCategoryName = newValue
                            },
                            textStyle = TextStyle(fontSize = 18.sp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    // Focus the value text field after entering the name
                                    // This will make it easier for the user to enter values.
                                }
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = if(newCategoryValueText == "") "" else newCategoryValueText,
                            placeholder = { Text("Enter a value in USD") },
                            onValueChange = { newValue ->
                                newCategoryValueText = newValue
                            },
                            textStyle = TextStyle(fontSize = 18.sp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    // Handle "Done" button click here
                                    val newValueDouble = newCategoryValueText.toDoubleOrNull() ?: 0.0
                                    newCategoryValue = newValueDouble

                                    // Add the category to the wealth data
                                    if (newCategoryName.isNotBlank() && newCategoryValue > 0.0) {
                                        val newCategory = CategoryData(newCategoryName, newCategoryValue)
                                        categories.add(newCategory)
                                        wealthData.categories = categories.toList()
                                        // Save updated wealth data to local storage
                                        saveWealthData(context, wealthData)
                                        // Clear the input fields
                                        newCategoryName = ""
                                        newCategoryValueText = ""
                                        // Close the dialog
                                        isDialogOpen = false
                                    }
                                }
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Handle the "Add" button click here
                            val newValueDouble = newCategoryValueText.toDoubleOrNull() ?: 0.0
                            newCategoryValue = newValueDouble

                            // Add the category to the wealth data
                            if (newCategoryName.isNotBlank() && newCategoryValue > 0.0) {
                                val newCategory = CategoryData(newCategoryName, newCategoryValue)
                                categories.add(newCategory)
                                wealthData.categories = categories.toList()
                                // Save updated wealth data to local storage
                                saveWealthData(context, wealthData)
                                // Clear the input fields
                                newCategoryName = ""
                                newCategoryValueText = ""
                                // Close the dialog
                                isDialogOpen = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Close the dialog
                            isDialogOpen = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )

        }
        }
        Box(modifier = Modifier
            .height(68.dp)
            .fillMaxWidth()) {
            Button(
                onClick = {
                    isDialogOpen = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Add Category", fontSize = 16.sp)
            }
        }

    }
}


// Function to calculate the current level based on progress
fun calculateCurrentLevel(totalWealth: Double, totalLevels: Int, initialTarget: Double): Int {
    // Define the progression factor (adjust as needed)
    val progressionFactor = 1.2

    // Calculate the current level based on progress
    var currentLevel = 1
    var target = initialTarget

    while (currentLevel <= totalLevels && totalWealth >= target) {
        currentLevel++
        target *= progressionFactor
    }

    return currentLevel
}
// Function to calculate the target for a specific level
fun calculateTargetForLevel(level: Int, initialTarget: Double): Double {
    // Define the progression factor (adjust as needed)
    val progressionFactor = 1.2

    // Calculate the target for the specified level
    var target = initialTarget
    repeat(level - 1) {
        target *= progressionFactor
    }

    return target
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryInput(
    categoryName: String,
    value: Double,
    existingValue: Double,
    context: Context,
    initialCategoryValue: Double = 0.0,
    onValueChange: (String, Double) -> Unit,
    onAddClick: (Double, String) -> Unit
) {
    var newCategoryValue by remember {
        mutableStateOf("")
    }
    var newCategoryName by remember {
        mutableStateOf(categoryName)
    }
    // Create a state for the displayed value
    var displayedValue by remember { mutableStateOf(value) }

    // Use a derived state to format and update the displayed value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
    ) {
        Text(
            text = "$categoryName : $displayedValue USD",
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 18.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicTextField(
                    value = newCategoryValue,
                    onValueChange = { newValue ->
                        val newValueDouble = newValue.toDoubleOrNull() ?: 0.0
                        newCategoryValue = newValueDouble.toString() // Update newCategoryValue
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 20.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Handle "Done" button click here
                            if (newCategoryName.isNotBlank() && newCategoryValue.toDouble() > 0.0) {
                                val updatedValue = existingValue + newCategoryValue.toDouble() // Calculate the updated value by adding
                                onValueChange(newCategoryName, updatedValue) // Call the lambda function with the updated value
                                newCategoryName = ""
                                newCategoryValue = ""
                                displayedValue = updatedValue // Update the displayed value
                            }
                        }
                    ),
                    decorationBox = @Composable { innerTextField ->
                        // places leading icon, text field with label and placeholder, trailing icon
                        TextFieldDefaults.TextFieldDecorationBox(
                            value = newCategoryValue,
                            visualTransformation = VisualTransformation.None,
                            innerTextField = innerTextField,
                            placeholder ={Text(" Enter a value in USD ")},
                            label = null,
                            leadingIcon = null,
                            trailingIcon = null,
                            supportingText = null,
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            enabled = true,
                            isError = false,
                            interactionSource = remember { MutableInteractionSource() },
                            colors = TextFieldDefaults.textFieldColors(),
                            contentPadding = PaddingValues(5.dp)
                            )
                        TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    }
                    )

                //Spacer(modifier = Modifier.width(16.dp))
              //  Box{
                Row( horizontalArrangement = Arrangement.End){
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank() && newCategoryValue.toDouble() > 0.0) {
                                val updatedValue = existingValue + newCategoryValue.toDouble() // Calculate the updated value by adding
                                onValueChange(newCategoryName, updatedValue) // Call the lambda function with the updated value
                                newCategoryName = ""
                                newCategoryValue = ""
                                displayedValue = updatedValue // Update the displayed value
                            }
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 16.dp)
                            )
                            .height(36.dp)
                            .wrapContentWidth() // Adjust the button size as needed
                    ) {
                        Text("+", fontSize = 16.sp)
                    }
                    Spacer( modifier = Modifier.width(1.dp))

                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank() && newCategoryValue.toDouble() > 0.0) {
                                val updatedValue =
                                    existingValue - newCategoryValue.toDouble() // Calculate the updated value by subtracting
                                onValueChange(
                                    newCategoryName,
                                    updatedValue
                                ) // Call the subtraction lambda function with the updated value
                                newCategoryName = ""
                                newCategoryValue = ""
                                displayedValue = updatedValue // Update the displayed value
                            }
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp)
                            )
                            .height(36.dp)
                            .wrapContentWidth() // Adjust the button size as needed
                    ) {
                        Text("-", fontSize = 16.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

