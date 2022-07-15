package com.hearthmanagement;

import com.hearthmanagement.IHearthListener;

interface IHearthDeviceSdk {
      void installAppPackage(String url, String md5sum, IHearthListener callback);
      void installOtaPackage(String url, String md5sum, IHearthListener callback);
}