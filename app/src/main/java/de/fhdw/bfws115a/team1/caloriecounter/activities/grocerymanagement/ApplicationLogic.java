package de.fhdw.bfws115a.team1.caloriecounter.activities.grocerymanagement;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import de.fhdw.bfws115a.team1.caloriecounter.R;
import de.fhdw.bfws115a.team1.caloriecounter.database.DatabaseEntityManager;
import de.fhdw.bfws115a.team1.caloriecounter.database.DatabaseGrocery;
import de.fhdw.bfws115a.team1.caloriecounter.database.DatabaseHelper;
import de.fhdw.bfws115a.team1.caloriecounter.entities.Grocery;
import de.fhdw.bfws115a.team1.caloriecounter.entities.GroceryUnit;
import de.fhdw.bfws115a.team1.caloriecounter.entities.Unit;
import de.fhdw.bfws115a.team1.caloriecounter.utilities.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niklas Lammers
 */
public class ApplicationLogic {

    /* Member variables */
    private Data mData;
    private Gui mGui;
    private ListAdapter mListAdapter;

    public ApplicationLogic(Data data, Gui gui) {
        mData = data;
        mGui = gui;
        initGui();
        initListener();
        initAdapter();
    }

    /**
     * Initialization.
     */
    private void initListener() {
        ClickListener cl = new ClickListener(this);
        mGui.getAddNewQuantityButton().setOnClickListener(cl);
        mGui.getSaveGrocery().setOnClickListener(cl);
        mGui.getGroceryName().addTextChangedListener(new TextChangeListener(this, mGui.getGroceryName()));
        mGui.getNewUnitAmount().addTextChangedListener(new TextChangeListener(this, mGui.getNewUnitAmount()));
        mGui.getGroceryCalories().addTextChangedListener(new TextChangeListener(this, mGui.getGroceryCalories()));
        mGui.getUnitSpinner().setOnItemSelectedListener(new SpinnerItemSelectListener(this, mData));
    }

    /**
     * Initialization.
     */
    private void initGui() {
        mGui.setGroceryNameText(mData.getGroceryName());
        mGui.setGroceryCaloriesAmount(mData.getGroceryCalories());
    }

    /**
     * Initialization.
     */
    private void initAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mData.getActivity(), android.R.layout.simple_list_item_1, mData.getUnitList());
        mGui.getUnitSpinner().setAdapter(adapter);

        mListAdapter = new ListAdapter(mData, this);
        mGui.getGroceryUnitsList().setAdapter(mListAdapter);
    }

    /**
     * If called, it creates a new quantity unit of the selected grocery entity.
     * The added new quantity unit will also be saved in the personal database.
     */
    public void onAddNewQuantityClicked() {
        Log.d("Debug", "onAdd Clicked: " + mData.getNewUnitAmount());
        if (Validation.checkNumberValue(mData.getNewUnitAmount())) {
            Log.d("Debug", "onAdd Clicked2");
            mData.getGroceryUnits().add(new GroceryUnit(new Unit(mData.getNewUnitName()), mData.getNewUnitAmount()));
            mListAdapter.notifyDataSetChanged();
        } else {
            Context context = mData.getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context, R.string.selectamount_emptyamounttoast, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * If called, deletes the selected quantity unit.
     * It also deletes the selected quantity unit from the selected grocery entity.
     */
    public void onDeleteQuantityClicked() {
    }

    /**
     * If called, checks if grocery name, quantity unit(s) and KCAL-amount of the selected grocery entity are valid.
     * It also checks if the specific grocery entity is already saved in the personal database.
     * Otherwise saves a new grocery entity (with corresponding attributes) in the personal database.
     */
    public void onSaveGroceryClicked() {
        if (mData.getInputGrocery() == null) {
            createNewGrocery();
        } else {
            editGrocery();
        }
    }

    /**
     * Creates a new grocery and adds it to the personal database.
     */
    public void createNewGrocery() {
        DatabaseEntityManager databaseEntityManager;
        databaseEntityManager = mData.getDatabaseEntityManager();

        if (Validation.checkLenght(DatabaseHelper.MEDIUM_NAME_LENGTH, mData.getGroceryName())
                && databaseEntityManager.isGroceryNameAvailable(mData.getGroceryName())) {

            if (Validation.checkNumberValue(mData.getGroceryCalories())) {
                Grocery newGrocery;
                newGrocery = new Grocery(mData.getGroceryName(), mData.getGroceryCalories());
                for (GroceryUnit gu : mData.getGroceryUnits()) {
                    newGrocery.addGroceryUnit(gu);
                }
                DatabaseGrocery databaseGrocery = databaseEntityManager.createGrocery(newGrocery);
            } else {
                Context context;
                Toast toast;
                context = mData.getActivity().getApplicationContext();
                toast = Toast.makeText(context, R.string.selectamount_emptyamounttoast, Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Context context;
            Toast toast;
            context = mData.getActivity().getApplicationContext();
            toast = Toast.makeText(context, R.string.quantityunitmanagement_existinggroceryindbtoast, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Edits an existing grocery and updates it in the personal database.
     */
    public void editGrocery() {
        DatabaseEntityManager databaseEntityManager;
        databaseEntityManager = mData.getDatabaseEntityManager();
        if (Validation.checkLenght(DatabaseHelper.MEDIUM_NAME_LENGTH, mData.getGroceryName())
                && (mData.getInputGrocery().getName() == mData.getGroceryName()
                || (databaseEntityManager.isGroceryNameAvailable(mData.getGroceryName()) && mData.getInputGrocery().getName() != mData.getGroceryName()))) {

            if (Validation.checkNumberValue(mData.getGroceryCalories())) {
                mData.getInputGrocery().setName(mData.getGroceryName());
                mData.getInputGrocery().setKcal(mData.getGroceryCalories());
                for (GroceryUnit gu : mData.getInputGrocery().getGroceryUnits()) {
                    mData.getInputGrocery().removeGroceryUnit(gu);
                }
                for (GroceryUnit gu : mData.getGroceryUnits()) {
                    mData.getInputGrocery().addGroceryUnit(gu);
                }
                databaseEntityManager.saveGrocery(mData.getInputGrocery());
            } else {
                Context context;
                Toast toast;
                context = mData.getActivity().getApplicationContext();
                toast = Toast.makeText(context, R.string.selectamount_emptyamounttoast, Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Context context;
            Toast toast;
            context = mData.getActivity().getApplicationContext();
            toast = Toast.makeText(context, R.string.quantityunitmanagement_existinggroceryindbtoast, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Allows to set the name of a grocery.
     *
     * @param name The new name the grocery should get.
     */
    public void onGroceryNameChanged(String name) {
        mData.setGroceryName(name);
    }

    /**
     * Allows to set the KCAL of a specific grocery.
     *
     * @param name The KCAl of a specific grocery.
     */
    public void onGroceryCaloriesChanged(String name) {
        try {
            mData.setGroceryCalories(Integer.parseInt(name));
        } catch (Exception e) {
            mData.setGroceryCalories(0);
        }
    }

    /**
     * Allows to set the name of a specific grocery.
     *
     * @param name The name of a specific grocery.
     */
    public void onNewUnitNameChanged(String name) {
        mData.setNewUnitName(name);
    }

    /**
     * Allows to set the unit amount of a specific grocery.
     *
     * @param amount The unit amount of a specific grocery.
     */
    public void onNewUnitAmountChanged(String amount) {
        try {
            mData.setNewUnitAmount(Double.parseDouble(amount));
        } catch (Exception e) {
            mData.setNewUnitAmount(0.0);
        }
    }
}