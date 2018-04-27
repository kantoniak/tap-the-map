package com.kantoniak.discrete_fox;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.kantoniak.discrete_fox.scene.AssetLoader.getCountryBaseObjName;
import static com.kantoniak.discrete_fox.scene.AssetLoader.getCountryNameDrawableName;
import static com.kantoniak.discrete_fox.scene.AssetLoader.getCountryTopObjName;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class AssetExistenceTest {
    @Test
    public void containCountryAssets() {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        String packageName = context.getPackageName();

        CountryUtil.allCountriesEurostatThenIso.forEach((String code) -> {
            assertNotEquals(getCountryBaseObjName(code) + " missing", 0, resources.getIdentifier(getCountryBaseObjName(code), "raw", packageName));
            assertNotEquals(getCountryTopObjName(code) + " missing", 0, resources.getIdentifier(getCountryTopObjName(code), "raw", packageName));
            assertNotEquals(getCountryNameDrawableName(code) + " missing", 0, resources.getIdentifier(getCountryNameDrawableName(code), "drawable", packageName));
        });
    }
}
