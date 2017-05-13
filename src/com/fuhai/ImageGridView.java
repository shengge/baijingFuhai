package com.fuhai;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageGridView extends Activity{

	 private static final String TAG = "ImageGridView";
	 String[] albums=null;
	 int flag=2;
	 private GridView gView;
	 private String rPath="";
	 private LinkedList<String> extens=new LinkedList<String>();
	 private List<String> picPathList = new ArrayList<String>();
	 @Override  
	 public void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);  
	    setContentView(R.layout.grid_view);  
	     gView = (GridView) this.findViewById(R.id.gridview);  
	    initData();
	   initListener(); 
	 }

	private void initData() {
		rPath = getRootPicPath();
        String name = "fuhai";  
        getAllUsbHostImages(rPath);
        albums= (String[])picPathList.toArray(new String[picPathList.size()]);
        gView.setAdapter(new GridViewAdapter(this,flag, name, getNames(flag, albums)));
        
	}
	
    public void getExtens()
	{
		extens.add(".JPEG");
		extens.add(".JPG");
		extens.add(".PNG");
		extens.add(".GIF");
		extens.add(".BMP");
	}
    
	  //get all usbhost images
    private void getAllUsbHostImages(String path) {
	// TODO Auto-generated method stub
    	flag=2;
    	extens=new LinkedList<String>();
    	getExtens();
    	File file=new File(path);
        getAllUsbHostImageFile(file);
    }
    public void getAllUsbHostImageFile(File file){
		
	  	file.listFiles(new FileFilter(){
				public boolean accept(File file) {
					String name = file.getName();
					int i = name.lastIndexOf('.');
					if(i != -1){
						name = name.substring(i).toUpperCase();
						if(extens.contains(name)){
							savePicture(file);
							return true;
						}
					}else if(file.isDirectory()){
						getAllUsbHostImageFile(file);
					}
					return false;
				}

				private void savePicture(File file) {
					String path=file.getAbsolutePath();
					picPathList.add(path);
				}
	  	});
	  }
	private void initListener() {
		 gView.setOnItemClickListener(new OnItemClickListener(){  
	           public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) 
	           {  
	                 //Log.i("GridView.setOnItemClickListener", "position="+position);
	                 //String path=images.get(position).path;
	        		 //Log.i("ImageListView_onListItemClick", "the path="+path);

	        		 ArrayList<String> pathArray=new ArrayList<String>();
	        		 for(int i=0; i<albums.length; i++)
	        		 {
	        			 if(flag==2)
	        			 {
	        				 pathArray.add(albums[i]);
	        			 }
	        			 else
	        			 {
	        			     String absolutePath=albums[i].split("&")[1];
	        			     Log.i(TAG, "absolutePath="+absolutePath);
	        			     pathArray.add(absolutePath);
	        			 }
	        		 }
	        		 
	        		 Intent intent = new Intent();  
	        		 intent.setClass(ImageGridView.this, ImageGalleryView.class);  
	        		 intent.putExtra("path", rPath);
	        		 intent.putExtra("id", position);
	        		 intent.putExtra("data", (String[])pathArray.toArray(new String[pathArray.size()]));
	        		 Log.i("ImageGridView_setOnItemClickListener", "position="+position+"; path="+rPath);
	        		 ImageGridView.this.startActivity(intent);  
	           }    
	     }); 
	}

	private String getRootPicPath() {
		String rootPath ="";
		String[] paths = getFilePath();
		 for(int i=0;i<paths.length;i++){
		    	String pth = paths[i];
		    	rootPath=pth+"/fuhai";
		    File file = new File(rootPath);
		    if(file.exists())
		    	return rootPath;
		    }
		 return rootPath;
	}

	  private String[] getFilePath() {
		  String[] result = null;  
		  StorageManager storageManager = (StorageManager)getSystemService(Context.STORAGE_SERVICE);  
		  try {  
		      Method method = StorageManager.class.getMethod("getVolumePaths");  
		      method.setAccessible(true);  
		      try {  
		          result =(String[])method.invoke(storageManager);  
		      } catch (InvocationTargetException e) {  
		          e.printStackTrace();  
		      }  
		      for (int i = 0; i < result.length; i++) {  
		          System.out.println("path----> " + result[i]+"\n");  
		      }  
		  } catch (Exception e) {  
		      e.printStackTrace();  
		  }  
		  return result;
	}
	private int[] getRowIds(String[] albums)
	{
		int[] ids=new int[albums.length];
		for(int i=0; i<albums.length; i++)
		{
			
			String id=albums[i].split("&")[0];
			ids[i]=Integer.valueOf(id);
		}
		return ids;
	}
	
	private String[] getNames(int flag, String[] albums)
	{
		if(flag==0)
		{
			Log.i(TAG, "----code comes to here----");
		   String[] paths=new String[albums.length];
		   String path=null;
		   String name=null;
		   for(int i=0; i<albums.length; i++)
		   {
			 path=albums[i].split("&")[1];
			 name=path.substring(path.lastIndexOf("/")+1);
			 Log.i(TAG, "path="+path+"; name="+name);
			 paths[i]=name;
		   }
		   return paths;
		}
		else if(flag==1)
		{
			String[] ids=new String[albums.length];
			for(int i=0; i<albums.length; i++)
			{
				String id=albums[i].split("&")[0];
				ids[i]=id;
			}
			return ids;
		}
		else 
			return albums;
	}
}
