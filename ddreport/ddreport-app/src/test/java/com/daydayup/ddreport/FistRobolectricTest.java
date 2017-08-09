package com.daydayup.ddreport;

import android.content.Context;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class FistRobolectricTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    /**
     * Convience method - to access the application context.
     * @return
     */
    protected Context getContext() {
        return RuntimeEnvironment.application;
    }

    @Test
    public void test(){
        System.out.println("tested");
        Context context = this.getContext();

        //MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        //activity.findViewById(R.id.action_search).performClick();

        //Intent expectedIntent = new Intent(activity, SearchActivity.class);

        //assertThat(shadowOf(activity).getNextStartedActivity());


        //Assert.assertTrue("onpurpose",false);

    }
}
