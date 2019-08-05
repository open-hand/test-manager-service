package io.choerodon.test.manager.infra.util;
   public class LongUtils {

       private LongUtils() {
       }

       public static boolean isUserId(Long userId){
           return userId != null && ! userId.equals(0L);
   }
}
