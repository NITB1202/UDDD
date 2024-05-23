package com.example.uddd.API;

import com.example.uddd.Domains.CommentDomain;
import com.example.uddd.Domains.PopularDomain;
import com.example.uddd.Domains.SavedDomain;
import com.example.uddd.Models.PlacesInfo;
import com.example.uddd.Models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface api {
    @FormUrlEncoded
    @POST("/api/createUser")
    Call<User> createUser(
            @Field("name") String name,
            @Field("dob") String dob,
            @Field("email") String email,
            @Field("password") String Password
    );
    @GET("/api/userLogin")
    Call<User> LoginUser(
            @Query("email") String email,
            @Query("pass") String pass
    );

    @GET("/search/searchbox/v1/category/coffee")
    Call<PlacesInfo> GetNearbyCoffee(
            @Query("access_token") String access_token,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("proximity") String proximity
    );
    @GET("/search/searchbox/v1/category/restaurant")
    Call<PlacesInfo> GetNearbyRestaurant(
            @Query("access_token") String access_token,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("proximity") String proximity
    );
    @GET("/search/searchbox/v1/category/food_and_drink")
    Call<PlacesInfo> GetNearbyFoodAndDrink(
            @Query("access_token") String access_token,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("proximity") String proximity
    );
    @GET("/search/searchbox/v1/category/shopping")
    Call<PlacesInfo> GetNearbyShop(
            @Query("access_token") String access_token,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("proximity") String proximity
    );
    @GET("/search/searchbox/v1/category/hotel")
    Call<PlacesInfo> GetNearbyHotel(
            @Query("access_token") String access_token,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("proximity") String proximity
    );
    @GET("/search/geocode/v6/forward")
    Call<PlacesInfo> geocode(
            @Query("q") String placeName,
            @Query("access_token") String access_token
    );
    @GET("/api/popularPlace")
    Call<ArrayList<PopularDomain>> getPopular();
    @GET("/api/getRecommend")
    Call<ArrayList<PopularDomain>> getRecommend();
    @GET("/api/getUserByID")
    Call<User> getUserByID(
            @Query("userID") int userID
    );
    @GET("/api/getComment")
    Call<ArrayList<CommentDomain>> getComment(
            @Query("ID") int locationID
    );
    @POST("/api/editName")
    Call<Void> editName(
            @Query("userID") int userID,
            @Query("newname") String newName
    );
    @POST("/api/editDob")
    Call<Void> editDob(
            @Query("userID") int userID,
            @Query("newdob") String newDob
    );
    @POST("/api/editAvatar")
    Call<Void> editAvatar(
            @Query("userID") int userID,
            @Query("avatarlink") String newAvatarLink
    );
    @POST("/api/postComment")
    Call<Void> postComment(
            @Query("userID") int userID,
            @Query("locationID") int locationID,
            @Query("date") String date,
            @Query("numStar") float numStar,
            @Query("content") String content
    );
    @POST("/api/likeComment")
    Call<Void> likeComment(
            @Query("id") int id
    );
    @POST("/api/dislikeComment")
    Call<Void> dislikeComment(
            @Query("id") int id
    );
    @POST("/api/undoLike")
    Call<Void> undoLike(
            @Query("id") int id
    );
    @POST("/api/undoDislike")
    Call<Void> undoDislike(
            @Query("id") int id
    );
    @GET("/api/getVungtauFood")
    Call<ArrayList<PopularDomain>> getVungTauFood();
    @GET("/api/getVungtauShop")
    Call<ArrayList<PopularDomain>> getVungTauShop();
    @GET("/api/getFavourite")
    Call<ArrayList<PopularDomain>> getFavour(
            @Query("id") int userID
    );
    @POST("/api/addFavourite")
    Call<Void> addFavour(
            @Query("userID") int userID,
            @Query("locationID") int locationID
    );
    @GET("/api/getSavedLocation")
    Call<ArrayList<SavedDomain>> getSavedLocation(
            @Query("userID") int userID
    );

    @POST("/api/addSavedLocation")
    Call<Void> addSaved(
            @Query("userID") int userID,
            @Query("name") String name,
            @Query("address") String address
    );
    @DELETE("/api/deleteSaved")
    Call<Void> deleteSaved(
            @Query("userID") int userID,
            @Query("name") String name
    );

}
