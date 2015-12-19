/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.examples;

/**
 * Created by Maxim S. on 19.12.2015.
 */
public class ExampleViewPager {

//    <android.support.v4.view.ViewPager
//    android:layout_width="match_parent"
//    android:layout_height="match_parent"
//    android:id="@+id/vpPhoto" />
//
//    <ru.maxdestroyer.utils.view.CirclePageIndicator
//    android:id="@+id/indicator"
//    android:layout_width="fill_parent"
//    app:fillColor="@color/colorPrimary"
//    app:pageColor="#b7c0ca"
//    android:layout_height="wrap_content"
//    android:layout_alignBottom="@+id/vpPhoto"
//    android:paddingBottom="10dp"/>

//    @Bind(R.id.vpPhoto)
//    ViewPager viewPager;
//
//    @Bind(R.id.indicator)
//    CirclePageIndicator indic;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.a_fullscreen_image);
//        ButterKnife.bind(this);
//
//
//        int id = getIntent().getIntExtra("gallery_id", 0);
//        Gallery gallery = Gallery.loadById(id);
//        ArrayList<GalleryItem> items = (ArrayList<GalleryItem>) gallery.getItems();
//
//        final ArrayList<View> pages = new ArrayList<>();
//
//        for (int i = 0; i < items.size(); i++) {
//            View page = getLayoutInflater().inflate(R.layout.page_fullscreen_image,
//                    null);
//            ImageView iv = (ImageView) page.findViewById(R.id.ivImage);
//            iv.setTag(items.get(i));
//
//            pages.add(page);
//        }
//
//        SamplePagerAdapter vpAdapter = new SamplePagerAdapter(pages);
//
//        viewPager.setAdapter(vpAdapter);
//
//        final int initPos = getIntent().getIntExtra("position", 0);
//        indic.setViewPager(viewPager, initPos);
//        // viewpager сохраняет свое состояние и грузит его
//        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener()
//                {
//                    @Override
//                    public void onGlobalLayout()
//                    {
//                        viewPager.setCurrentItem(initPos);
//                        indic.onPageSelected(initPos);
//                        viewPager.getViewTreeObserver()
//                                .removeOnGlobalLayoutListener(this);
//                    }
//                }
//        );
//
//        indic.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
//        {
//            @Override
//            public void onPageScrolled(int i, float v, int i1)
//            {
//
//            }
//
//            @Override
//            public void onPageSelected(int i)
//            {
//                final View page = pages.get(i);
//                final ImageView iv = (ImageView) page.findViewById(R.id.ivImage);
//                GalleryItem item = (GalleryItem) iv.getTag();
//
//                String url = item.image;
//                String preview = item.preview;
//                // already loaded
//                ImageLoader.getInstance().displayImage(preview, iv);
//                // load
//                ImageLoader.getInstance().displayImage(url, iv);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i)
//            {
//
//            }
//        });
//    }
}
