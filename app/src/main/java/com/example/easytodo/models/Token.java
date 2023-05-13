package com.example.easytodo.models;

public class Token {
    public static String access;
    public static String refresh;

//    public static void refreshToken() {
//        final String BASE_URL = "http://10.0.2.2:8000/api/";
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        UserAPI userAPI = retrofit.create(UserAPI.class);
//        Map<String, String> refresh = Map.of("refresh", Token.refresh);
//        H.enqueueReq(userAPI.refreshToken(refresh), ((call, response) -> {
//            System.out.println(response.body().getAccess());
//        }));
//    }
}
