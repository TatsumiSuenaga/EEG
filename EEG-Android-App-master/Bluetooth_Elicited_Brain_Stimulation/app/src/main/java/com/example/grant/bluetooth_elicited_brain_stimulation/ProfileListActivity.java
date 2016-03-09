package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ProfileListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ProfileListActivityFragment();
    }
}
