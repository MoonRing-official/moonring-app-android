package com.module.common.support.config;


import com.module.common.BuildConfig;
import com.module.common.enums.PackagingConfig;


public final class AppConfig {



   public static Enviroment enviroment = getPackagingConfigEnviroment();



    public static Enviroment getPackagingConfigEnviroment(){
        if (BuildConfig.PACKAGING_CONFIG == PackagingConfig.DEV.getTpye()){
            return  Enviroment.beta;
        }

        if ( BuildConfig.PACKAGING_CONFIG == PackagingConfig.PRODA.getTpye()
                || BuildConfig.PACKAGING_CONFIG == PackagingConfig.PRODB.getTpye()
                || BuildConfig.PACKAGING_CONFIG == PackagingConfig.PRODC.getTpye()
                || BuildConfig.PACKAGING_CONFIG == PackagingConfig.PRODD.getTpye()
        ){
            return  Enviroment.proda;
        }
        return Enviroment.prod;
    }

    public static void setEnviroment(Enviroment enviroment) {
        AppConfig.enviroment = enviroment;
    }

    public static Enviroment getEnviroment() {
        return enviroment;
    }




    public static String getWebUrl() {




        return enviroment.webUrl;
    }


    public static String getHostUrl() {




        return enviroment.url;
    }

    public enum Enviroment{
        dev("",""),

        beta("",""),
        prod("",""),
        proda("","");

        private String url;


        private String webUrl;


        Enviroment(String url, String webUrl) {
            this.url = url;
            this.webUrl = webUrl;
        }



        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }


        public String getWebUrl() {
            return webUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}