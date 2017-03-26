package com.ase_1617.organizedlib.scribejava;

import com.ase_1617.organizedlib.utility.Constants;
import com.github.scribejava.core.builder.api.DefaultApi20;

/**
 * Created by bob on 25.03.17.
 */

public class OrganizedOAuth20Api extends DefaultApi20{
    protected OrganizedOAuth20Api() {
    }

    private static class InstanceHolder {
        private static final OrganizedOAuth20Api INSTANCE = new OrganizedOAuth20Api();
    }

    public static OrganizedOAuth20Api instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return Constants.serverUrlBase + ":8000/o/token/";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return Constants.serverUrlBase + ":8000/o/token/";
    }
}
