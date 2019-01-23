package com.it_tech613.zhe.instagramunfollow.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.it_tech613.zhe.instagramunfollow.R;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramUnfollowRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoggedUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class PreferenceManager extends MultiDexApplication {
    public static SharedPreferences.Editor editor;
    public static SharedPreferences settings;
    public static Instagram4Android instagram;
    public static ArrayList<InstagramUserSummary> followers;
    public static ArrayList<InstagramUserSummary> following;
    public static ArrayList<InstagramUserSummary> unfollowers;
    public static ArrayList<InstagramUserSummary> whitelist;
    public static InstagramLoggedUser currentUser;
    public static ArrayList<InstagramFeedItem> feedItems;
    public static ArrayList<Boolean> list_redeemed=new ArrayList<>();
    private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy:MM:dd_hh:mm");
    public static int free_limit_perday=100;
    public static int free_limit_perhour=100;
    private static String seperater=";;";
    public static File myDir;
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO APP ID
        MobileAds.initialize(this, "ca-app-pub-7166764673125229~3007945136");
        settings = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        editor = settings.edit();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        unfollowers = new ArrayList<>();
        whitelist=new ArrayList<>();
        myDir = new File(Environment.getExternalStorageDirectory() +File.separator+ getString(R.string.app_name));//Constant.sp+getString(R.string.app_name)
        try{
            if(myDir.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void logoutManager(){
        setIsSaved(false);
        setUserName("");
        setPassword("");
        unfollowers=new ArrayList<>();
        whitelist=new ArrayList<>();
        following=new ArrayList<>();
        followers=new ArrayList<>();
        currentUser=new InstagramLoggedUser();
        feedItems=new ArrayList<>();
    }

    public static void checkLimit(){

        Date last_login=new Date();
        try {
            last_login=dateFormat.parse(getLastLogin());
            Date valid_date=new Date(last_login.getTime()+(24*60*60*1000));
            //if one day more time passed
            if (valid_date.before(new Date())) {
                setFreeLimit(free_limit_perday);
                ArrayList<Boolean> list_redeemed=new ArrayList<>();
                list_redeemed.add(false);
                list_redeemed.add(false);
                list_redeemed.add(false);
                list_redeemed.add(false);
                list_redeemed.add(false);
                list_redeemed.add(false);
                list_redeemed.add(false);
                list_redeemed.add(false);
                setListRedeemed(list_redeemed);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static Set<String> getWhitelist_ids() {
        String key=getUserName();
        return settings.getStringSet(key,new HashSet<String>());
    }

    public static void setWhitelist_ids(Set<String> whitelist_ids) {
        String key=getUserName();
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,whitelist_ids);
        editor.apply();
    }

    public static void addWhitelist_ids(long id, InstagramUserSummary userSummary){
        ArrayList<String> whitelist_ids=new ArrayList<>(getWhitelist_ids());
        if (whitelist_ids.contains(String.valueOf(id))) return;
        whitelist_ids.add(0, String.valueOf(id));
        setWhitelist_ids(new HashSet<String>(whitelist_ids));
        whitelist.add(0, userSummary);
    }

    public static void removeWhitelist_ids(long id, InstagramUserSummary userSummary){
        Set<String> whitelist_ids=getWhitelist_ids();
        whitelist_ids.remove(String.valueOf(id));
        setWhitelist_ids(whitelist_ids);
        unfollowers.add(0, userSummary);
    }

    public static ArrayList<String> getUnfollwed24_ids() {
        String key=getUserName()+seperater+"24";
        Set<String> Unfollowed_ids=settings.getStringSet(key,new HashSet<String>());
        ArrayList<String> unfollow24_ids=new ArrayList<>(Unfollowed_ids);
        for (int i=0;i<unfollow24_ids.size();i++){
            String time=unfollow24_ids.get(i).split(seperater)[1];
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            Date one_day_ago=new Date(System.currentTimeMillis() - (DAY_IN_MS));
            try {
                Date that_time=dateFormat.parse(time);
                if (that_time.before(one_day_ago)) unfollow24_ids.remove(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return unfollow24_ids;
    }

    public static void addUnfollwed24_ids(long id){
        String key=getUserName()+seperater+"24";
        Set<String> Unfollowed24_ids=new HashSet<String>(getUnfollwed24_ids());
        Date now=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy:MM:dd_hh:mm");
        Unfollowed24_ids.add(String.valueOf(id)+seperater+dateFormat.format(now));
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,Unfollowed24_ids);
        editor.apply();
    }

    public static ArrayList<String> getUnfollwed1_ids() {
        String key=getUserName()+seperater+"1";
        Set<String> Unfollowed_ids=settings.getStringSet(key,new HashSet<String>());
        ArrayList<String> unfollow1_ids=new ArrayList<>(Unfollowed_ids);
        for (int i=0;i<unfollow1_ids.size();i++){
            String time=unfollow1_ids.get(i).split(seperater)[1];
            long DAY_IN_MS = 1000 * 60 * 60;
            Date one_hour_ago=new Date(System.currentTimeMillis() - (DAY_IN_MS));
            try {
                Date that_time=dateFormat.parse(time);
                if (that_time.after(one_hour_ago)) unfollow1_ids.remove(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return unfollow1_ids;
    }

    public static void addUnfollwed1_ids(long id){
        String key=getUserName()+seperater+"1";
        Set<String> Unfollowed1_ids=new HashSet<String>(getUnfollwed1_ids());
        Date now=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy:MM:dd_hh:mm");
        Unfollowed1_ids.add(String.valueOf(id)+seperater+dateFormat.format(now));
        editor.remove(key);
        editor.apply();
        editor.putStringSet(key,Unfollowed1_ids);
        editor.apply();
    }
    public static void setUserName(String userName){
        editor.remove("username");
        editor.apply();
        editor.putString("username",userName);
        editor.apply();
    }

    public static String getUserName(){
        return settings.getString("username","");
    }

    public static void setPassword(String password){
        editor.remove("password");
        editor.apply();
        editor.putString("password",password);
        editor.apply();
    }

    public static String getPassword(){
        return settings.getString("password","");
    }

    public static void setIsSaved(boolean saved){
        editor.remove("saved");
        editor.apply();
        editor.putBoolean("saved",saved);
        editor.apply();
    }

    public static boolean isSaved(){
        return settings.getBoolean("saved",false);
    }

    public static UnfollowStatus unfollow(InstagramUserSummary userSummary){
        try {
            int limit=getFreeLimit()+getRewardLimit();
            Log.e("freelimit",getFreeLimit()+"");
            Log.e("rewardlimit",getRewardLimit()+"");
            if (limit == 0) return UnfollowStatus.limited;
            if (getUnfollwed1_ids().size()>=free_limit_perhour) return UnfollowStatus.limited_per_hour;
            instagram.sendRequest(new InstagramUnfollowRequest(userSummary.getPk()));
            following.remove(userSummary);
            addUnfollwed24_ids(userSummary.getPk());
            addUnfollwed1_ids(userSummary.getPk());
            if (getRewardLimit()!=0) setRewardLimit(getRewardLimit()-1);
            else if (getRewardLimit()==0 && getFreeLimit()!=0) setFreeLimit(getFreeLimit()-1);
            return UnfollowStatus.success;
        } catch (IOException e) {
            e.printStackTrace();
            return UnfollowStatus.failed;
        }
    }

    public static int getFreeLimit(){
        return settings.getInt(getUserName()+seperater+"free_limit",0);
    }

    public static void setFreeLimit(int limit){
        editor.remove(getUserName()+seperater+"free_limit");
        editor.apply();
        editor.putInt(getUserName()+seperater+"free_limit",limit);
        editor.apply();
    }

    public static int getRewardLimit(){
        return settings.getInt(getUserName()+seperater+"reward_limit",0);
    }

    public static void setRewardLimit(int limit){
        editor.remove(getUserName()+seperater+"reward_limit");
        editor.apply();
        editor.putInt(getUserName()+seperater+"reward_limit",limit);
        editor.apply();
    }

    public static String getLastLogin(){
        return settings.getString(getUserName() + seperater + "last_login","0000:00:00_00:00");
    }

    public static void setLastLogin(){
        Date now=new Date();
        editor.remove(getUserName() + seperater + "last_login");
        editor.apply();
        editor.putString(getUserName() + seperater + "last_login",dateFormat.format(now));
        editor.apply();
    }

    public static ArrayList<Boolean> getListRedeemed(){
        //1: true, 0: false- 8 reward ads redeemed list
        ArrayList<Boolean> list_redeemed=new ArrayList<>();
        String[] list = settings.getString(getUserName() + seperater + "list_redeemed","0,0,0,0,0,0,0,0").split(",");
        for (String aList : list) {
            if (aList.equals("0")) list_redeemed.add(false);
            else list_redeemed.add(true);
        }
        return list_redeemed;
    }

    public static void setListRedeemed(ArrayList<Boolean> listRedeemed){
        StringBuilder list= new StringBuilder();
        for (int i=0;i<listRedeemed.size();i++){
            if (i!=0) list.append(",");
            if (listRedeemed.get(i)) list.append("1");
            else list.append("0");
        }
        editor.remove(getUserName() + seperater + "list_redeemed");
        editor.apply();
        editor.putString(getUserName() + seperater + "list_redeemed", list.toString());
        editor.apply();
    }

    public static String getAccessToken(){
        return settings.getString(getUserName() + seperater + "access_token","");
    }

    public static void setAccessToken(String accessToken){
        editor.remove(getUserName() + seperater + "access_token");
        editor.apply();
        editor.putString(getUserName() + seperater + "access_token",accessToken);
        editor.apply();
    }
}
