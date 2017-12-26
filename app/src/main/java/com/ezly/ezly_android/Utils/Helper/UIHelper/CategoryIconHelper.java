package com.ezly.ezly_android.Utils.Helper.UIHelper;

import com.ezly.ezly_android.R;

import java.util.HashMap;

/**
 * Created by Johnnie on 16/10/16.
 */

public class CategoryIconHelper {
    /**
     * category images
     */
    private static final int IMAGE_DEFAULT;
    private static final int IMAGE_HANDYMAN;
    private static final int IMAGE_CLEANER;
    private static final int IMAGE_OTHERS;
    private static final int IMAGE_GARDENER;
    private static final int IMAGE_COMPUTER;
    private static final int IMAGE_MOVING;
    private static final int IMAGE_ELECTRICIAN;
    private static final int IMAGE_APPLIANCE_REPAIRERS;

    private static final int MARK_SMALL_IMAGE_DEFAULT;
    private static final int MARK_SMALL_IMAGE_HANDYMAN;
    private static final int MARK_SMALL_IMAGE_CLEANER;
    private static final int MARK_SMALL_IMAGE_GARDENER;
    private static final int MARK_SMALL_IMAGE_COMPUTER;
    private static final int MARK_SMALL_IMAGE_MOVING;
    private static final int MARK_SMALL_IMAGE_ELECTRICIAN;
    private static final int MARK_SMALL_IMAGE_OTHERS;
    private static final int MARK_SMALL_IMAGE_APPLIANCE_REPAIRERS;

    private static final int MARK_BIG_IMAGE_DEFAULT;
    private static final int MARK_BIG_IMAGE_HANDYMAN;
    private static final int MARK_BIG_IMAGE_CLEANER;
    private static final int MARK_BIG_IMAGE_GARDENER;
    private static final int MARK_BIG_IMAGE_COMPUTER;
    private static final int MARK_BIG_IMAGE_MOVING;
    private static final int MARK_BIG_IMAGE_ELECTRICIAN;
    private static final int MARK_BIG_IMAGE_OTHERS;
    private static final int MARK_BIG_IMAGE_APPLIANCE_REPAIRERS;

    private static final HashMap<String, Integer> CATEGORY_ID_TO_IMAGE_MAP;
    private static final HashMap<String, Integer> CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP;
    private static final HashMap<String, Integer> CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP;


    static {
        /**
         * category images
         */
        IMAGE_GARDENER            = R.drawable.ct_garden;
        IMAGE_HANDYMAN            = R.drawable.ct_house_fix;
        IMAGE_CLEANER             = R.drawable.ct_homework_cleaner;
        IMAGE_ELECTRICIAN         = R.drawable.ct_electrician;
        IMAGE_COMPUTER            = R.drawable.ct_computer;
        IMAGE_MOVING              = R.drawable.ct_moving_house;
        IMAGE_APPLIANCE_REPAIRERS = R.drawable.ct_appliancerepairers;
        IMAGE_DEFAULT             = R.drawable.ct_house_fix;
        IMAGE_OTHERS              = R.drawable.ct_others;

        MARK_SMALL_IMAGE_DEFAULT             = R.drawable.marker_small_house_fix;
        MARK_SMALL_IMAGE_HANDYMAN            = R.drawable.marker_small_house_fix;
        MARK_SMALL_IMAGE_CLEANER             = R.drawable.marker_small_homework_cleaner;
        MARK_SMALL_IMAGE_GARDENER            = R.drawable.marker_small_garden;
        MARK_SMALL_IMAGE_COMPUTER            = R.drawable.marker_small_computer;
        MARK_SMALL_IMAGE_MOVING              = R.drawable.marker_small_moving_house;
        MARK_SMALL_IMAGE_ELECTRICIAN         = R.drawable.marker_small_electrician;
        MARK_SMALL_IMAGE_OTHERS              = R.drawable.marker_small_house_fix;
        MARK_SMALL_IMAGE_APPLIANCE_REPAIRERS = R.drawable.marker_small_appliancerepairers;

        MARK_BIG_IMAGE_DEFAULT             = R.drawable.marker_big_house_fix;
        MARK_BIG_IMAGE_HANDYMAN            = R.drawable.marker_big_house_fix;
        MARK_BIG_IMAGE_CLEANER             = R.drawable.marker_big_homework_cleaner;
        MARK_BIG_IMAGE_GARDENER            = R.drawable.marker_big_garden;
        MARK_BIG_IMAGE_COMPUTER            = R.drawable.marker_big_computer;
        MARK_BIG_IMAGE_MOVING = R.drawable.marker_big_moving_house;
        MARK_BIG_IMAGE_ELECTRICIAN         = R.drawable.marker_big_electrician;
        MARK_BIG_IMAGE_OTHERS              = R.drawable.marker_big_others;
        MARK_BIG_IMAGE_APPLIANCE_REPAIRERS = R.drawable.marker_big_appliancerepairers;


        //TODO: switch to sparseArray after categoryID changed to a single int
        CATEGORY_ID_TO_IMAGE_MAP = new HashMap<>();
        CATEGORY_ID_TO_IMAGE_MAP.put("HouseRepair", IMAGE_HANDYMAN);
        CATEGORY_ID_TO_IMAGE_MAP.put("Cleaning", IMAGE_CLEANER);
        CATEGORY_ID_TO_IMAGE_MAP.put("Others", IMAGE_OTHERS);
        CATEGORY_ID_TO_IMAGE_MAP.put("Gardening", IMAGE_GARDENER);
        CATEGORY_ID_TO_IMAGE_MAP.put("ApplianceRepair", IMAGE_APPLIANCE_REPAIRERS);
        CATEGORY_ID_TO_IMAGE_MAP.put("WaterGasElectricity", IMAGE_ELECTRICIAN);
        CATEGORY_ID_TO_IMAGE_MAP.put("ComputerRepair", IMAGE_COMPUTER);
        CATEGORY_ID_TO_IMAGE_MAP.put("Moving", IMAGE_MOVING);


        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP = new HashMap<>();
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("HouseRepair", MARK_SMALL_IMAGE_HANDYMAN);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("Cleaning", MARK_SMALL_IMAGE_CLEANER);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("Others", MARK_SMALL_IMAGE_OTHERS);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("Gardening", MARK_SMALL_IMAGE_GARDENER);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("ApplianceRepair", MARK_SMALL_IMAGE_APPLIANCE_REPAIRERS);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("WaterGasElectricity", MARK_SMALL_IMAGE_ELECTRICIAN);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("ComputerRepair", MARK_SMALL_IMAGE_COMPUTER);
        CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.put("Moving", MARK_SMALL_IMAGE_MOVING);

        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP = new HashMap<>();
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("HouseRepair", MARK_BIG_IMAGE_HANDYMAN);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("Cleaning", MARK_BIG_IMAGE_CLEANER);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("Others", MARK_BIG_IMAGE_OTHERS);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("Gardening", MARK_BIG_IMAGE_GARDENER);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("ApplianceRepair", MARK_BIG_IMAGE_APPLIANCE_REPAIRERS);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("WaterGasElectricity", MARK_BIG_IMAGE_ELECTRICIAN);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("ComputerRepair", MARK_BIG_IMAGE_COMPUTER);
        CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.put("Moving", MARK_BIG_IMAGE_MOVING);
    }

    public static int getCategoryIcon(String categoryCode){
        if(!CATEGORY_ID_TO_IMAGE_MAP.keySet().contains(categoryCode)){
            //default icon
            return IMAGE_DEFAULT;
        }
        else{
            return CATEGORY_ID_TO_IMAGE_MAP.get(categoryCode);
        }
    }

    public static int getCategorySmallMarkerIcon(String categoryCode){
        if(!CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.keySet().contains(categoryCode)){
            //default icon
            return MARK_SMALL_IMAGE_DEFAULT;
        }
        else{
            return CATEGORY_ID_TO_MARKER_SMALL_IMAGE_MAP.get(categoryCode);
        }
    }

    public static int getCategoryBigMarkerIcon(String categoryCode){
        if(!CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.keySet().contains(categoryCode)){
            //default icon
            return MARK_BIG_IMAGE_DEFAULT;
        }
        else{
            return CATEGORY_ID_TO_MARKER_BIG_IMAGE_MAP.get(categoryCode);
        }
    }
}
