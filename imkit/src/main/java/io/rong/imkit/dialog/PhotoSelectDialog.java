package io.rong.imkit.dialog;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.custom.base.manager.SDActivityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.common.rlog.RLog;
import io.rong.imkit.R;
import io.rong.imkit.picture.PictureMediaScannerConnection;
import io.rong.imkit.picture.PictureSelector;
import io.rong.imkit.picture.adapter.PictureAlbumDirectoryAdapter;
import io.rong.imkit.picture.adapter.PictureImageGridAdapter;
import io.rong.imkit.picture.config.PictureConfig;
import io.rong.imkit.picture.config.PictureMimeType;
import io.rong.imkit.picture.config.PictureSelectionConfig;
import io.rong.imkit.picture.decoration.GridSpacingItemDecoration;
import io.rong.imkit.picture.dialog.PictureLoadingDialog;
import io.rong.imkit.picture.entity.LocalMedia;
import io.rong.imkit.picture.entity.LocalMediaFolder;
import io.rong.imkit.picture.model.LocalMediaLoader;
import io.rong.imkit.picture.observable.ImagesObservable;
import io.rong.imkit.picture.permissions.PermissionChecker;
import io.rong.imkit.picture.tools.DoubleUtils;
import io.rong.imkit.picture.tools.JumpUtils;
import io.rong.imkit.picture.tools.MediaUtils;
import io.rong.imkit.picture.tools.PictureFileUtils;
import io.rong.imkit.picture.tools.ScreenUtils;
import io.rong.imkit.picture.tools.SdkVersionUtils;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imkit.picture.widget.FolderPopWindow;
import io.rong.imkit.utils.RongUtils;

public class PhotoSelectDialog extends DialogFragment implements PictureImageGridAdapter.OnPhotoSelectChangedListener, PictureAlbumDirectoryAdapter.OnItemClickListener {

    protected static final int SHOW_DIALOG = 0;
    protected static final int DISMISS_DIALOG = 1;
    private static final String TAG = PhotoSelectDialog.class.getSimpleName();
    protected String cameraPath;
    protected List<LocalMediaFolder> foldersList = new ArrayList<>();

    protected List<LocalMedia> images = new ArrayList<>();

    private ConstraintLayout topBackContainer;
    private RecyclerView mPictureRecycler;
    private LinearLayout myPhotoContainer;
    private TextView myPhotoText;
    private View myPhotoLine;
    private LinearLayout privateAlbumContainer;
    private TextView privateAlbumText;
    private View privateAlbumLine;
    private ConstraintLayout privateAlbumContentContainer;
    private PictureSelectionConfig config;
    private PictureImageGridAdapter adapter;
    private LocalMediaLoader mediaLoader;

    protected FolderPopWindow folderWindow;
    private PictureLoadingDialog dialog;
    private List<LocalMedia> selectionMedias;


    public static PhotoSelectDialog newInstance() {

        PhotoSelectDialog dialogFragment = new PhotoSelectDialog();

        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);

        return dialogFragment;

    }

    @Override

    public void onAttach(Context context) {

        super.onAttach(context);

    }

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(PictureConfig.EXTRA_CONFIG);
            cameraPath = savedInstanceState.getString(PictureConfig.BUNDLE_CAMERA_PATH);
        } else {
            config = PictureSelectionConfig.getInstance();
        }
        loadAllMediaData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //设置界面
        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
        View view = inflater.inflate(R.layout.layout_photo, container);
        folderWindow = new FolderPopWindow(getActivity(), config);
//        folderWindow.setArrowImageView(mIvArrow);
        folderWindow.setOnItemClickListener(this);
        initView(view);
        initEvent();
        return view;

    }


    public void initView(View view) {
        topBackContainer = view.findViewById(R.id.top_back_container);
        mPictureRecycler = view.findViewById(R.id.my_photo_list);
        myPhotoContainer = view.findViewById(R.id.my_photo_container);
        myPhotoText = view.findViewById(R.id.my_photo_text);
        myPhotoLine = view.findViewById(R.id.my_photo_line);
        privateAlbumContainer = view.findViewById(R.id.private_album_container);
        privateAlbumText = view.findViewById(R.id.private_album_text);
        privateAlbumLine = view.findViewById(R.id.private_album_line);
        privateAlbumContentContainer = view.findViewById(R.id.private_album_content_container);
    }
    public void initEvent(){
        topBackContainer.setOnClickListener(view1 -> {
            dismissAllowingStateLoss();
        });

        myPhotoContainer.setOnClickListener(view -> {
            myPhotoText.setTextColor(Color.WHITE);
            myPhotoLine.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                myPhotoText.setTypeface(getContext().getResources().getFont(R.font.interbold));
            }

            privateAlbumText.setTextColor(Color.parseColor("#9C9FA6"));
            privateAlbumLine.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                privateAlbumText.setTypeface(getContext().getResources().getFont(R.font.interregular));
            }
            mPictureRecycler.setVisibility(View.VISIBLE);
            privateAlbumContentContainer.setVisibility(View.GONE);
        });
        privateAlbumContainer.setOnClickListener(view ->  {
            privateAlbumText.setTextColor(Color.WHITE);
            privateAlbumLine.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                privateAlbumText.setTypeface( getContext().getResources().getFont(R.font.interbold));
            }


            myPhotoText.setTextColor(Color.parseColor("#9C9FA6"));
            myPhotoLine.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                myPhotoText.setTypeface(getContext().getResources().getFont(R.font.interregular));
            }
            mPictureRecycler.setVisibility(View.GONE);
            privateAlbumContentContainer.setVisibility(View.VISIBLE);

        });

        selectionMedias =
                config.selectionMedias == null
                        ? new ArrayList<LocalMedia>()
                        : config.selectionMedias;
        mPictureRecycler.setHasFixedSize(true);
        mPictureRecycler.addItemDecoration(
                new GridSpacingItemDecoration(
                        config.imageSpanCount, ScreenUtils.dip2px(getContext(), 2), false));
        mPictureRecycler.setLayoutManager(
                new GridLayoutManager(getContext(), config.imageSpanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) mPictureRecycler.getItemAnimator())
                .setSupportsChangeAnimations(false);
        adapter = new PictureImageGridAdapter(getContext(), config);
        adapter.setOnPhotoSelectChangedListener(this);
        adapter.bindSelectImages(selectionMedias);
        mPictureRecycler.setAdapter(adapter);
        adapter.bindSelectImages(selectionMedias);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置窗口显示位置、大小

        Dialog dialog = getDialog();

        if (dialog != null) {

            Window window = dialog.getWindow();

            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

            lp.width = dm.widthPixels;
            lp.height = (int) (dm.heightPixels * 0.92);

            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM);


        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override

    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override

    public void onDestroy() {
        super.onDestroy();
    }


    @Override

    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onTakePhoto() {
        if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
            startCamera();
        } else {
            PermissionChecker.requestPermissions(
                    getActivity(),
                    new String[] {Manifest.permission.CAMERA},
                    PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {

    }
    /**
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        onResult(result);
    }
    @Override
    public void onPictureClick(LocalMedia media, int position) {
        if (config.selectionMode == PictureConfig.SINGLE && config.isSingleDirectReturn) {
            List<LocalMedia> list = new ArrayList<>();
            list.add(media);
            handlerResult(list);
        } else {
            List<LocalMedia> images = adapter.getImages();
            startPreview(images, position);
        }
    }
    /**
     * preview image and video
     *
     * @param previewImages
     * @param position
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        Bundle bundle = new Bundle();
        List<LocalMedia> selectedImages = adapter.getSelectedImages();
        ImagesObservable.getInstance().savePreviewMediaData(new ArrayList<>(previewImages));
        bundle.putParcelableArrayList(
                PictureConfig.EXTRA_SELECT_LIST, (ArrayList<? extends Parcelable>) selectedImages);
        bundle.putInt(PictureConfig.EXTRA_POSITION, position);
        JumpUtils.startPicturePreviewActivity(getContext(), bundle);
//        overridePendingTransition(R.anim.rc_picture_anim_enter, R.anim.rc_picture_anim_fade_in);
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        if (images == null) {
            return;
        }
        if (config.camera
                && config.selectionMode == PictureConfig.MULTIPLE
                && selectionMedias != null) {
            images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
        }
        if (config.isCheckOriginalImage) {
            int size = images.size();
            for (int i = 0; i < size; i++) {
                LocalMedia media = images.get(i);
                media.setOriginal(true);
            }
        }
        Intent intent = PictureSelector.putIntentResult(images);
        getActivity().setResult(RESULT_OK, intent);
    }



    /** 加载数据 */
    private void loadAllMediaData() {
        String[] permissions = null;
        if (RongUtils.checkSDKVersionAndTargetIsTIRAMISU(getActivity())) {
            permissions =
                    new String[] {
                            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
                    };
        } else {
            permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        if (PermissionChecker.checkSelfPermission(getContext(), permissions)) {
            mHandler.sendEmptyMessage(SHOW_DIALOG);
            readLocalMedia();
        } else {
            PermissionChecker.requestPermissions(
                    getActivity(), permissions, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    /** get LocalMedia s */
    protected void readLocalMedia() {
        if (mediaLoader == null) {
            mediaLoader = new LocalMediaLoader(getActivity(), config);
        }
        mediaLoader.loadAllMedia();
        mediaLoader.setCompleteListener(
                new LocalMediaLoader.LocalMediaLoadListener() {
                    @Override
                    public void loadComplete(List<LocalMediaFolder> folders) {
                        if (folders.size() > 0) {
                            foldersList = folders;
                            LocalMediaFolder folder = folders.get(0);
                            folder.setChecked(true);
                            List<LocalMedia> localImg = folder.getImages();
                            // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                            // 因为onActivityResult里手动添加拍照后的照片，
                            // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                            int size = images.size();
                            if (localImg.size() >= size) {
                                images = localImg;
                                folderWindow.bindFolder(folders);
                            }
                        }
                        if (adapter != null && images != null) {
                            adapter.bindImagesData(images);
                            boolean isEmpty = images.size() > 0;
                            if (!isEmpty) {

                            }
                        }
                        mHandler.sendEmptyMessage(DISMISS_DIALOG);
                    }

                    @Override
                    public void loadMediaDataError() {
                        mHandler.sendEmptyMessage(DISMISS_DIALOG);
                    }
                });
    }

    /** open camera */
    public void startCamera() {
        // 防止快速点击，但是单独拍照不管
        if (!DoubleUtils.isFastDoubleClick()) {
            switch (config.chooseMode) {
                case PictureConfig.TYPE_ALL:
                case PictureConfig.TYPE_IMAGE:
                    // 拍照
                    startOpenCamera();
                    break;
                default:
                    break;
            }
        }
    }
    /** start to camera、preview、crop */
    protected void startOpenCamera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Uri imageUri;
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    imageUri = MediaUtils.createImageUri(getActivity());
                    if (imageUri != null) {
                        cameraPath = imageUri.toString();
                    }
                } else {
                    int chooseMode =
                            config.chooseMode == PictureConfig.TYPE_ALL
                                    ? PictureConfig.TYPE_IMAGE
                                    : config.chooseMode;
                    File cameraFile =
                            PictureFileUtils.createCameraFile(
                                    getActivity(),
                                    chooseMode,
                                    config.cameraFileName,
                                    config.suffixType);
                    cameraPath = cameraFile.getAbsolutePath();
                    imageUri = PictureFileUtils.parUri(getActivity(), cameraFile);
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
            }
        } catch (Exception e) {
            RLog.i(TAG, e.getMessage());
        }
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler =
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case SHOW_DIALOG:
                            showPleaseDialog();
                            break;
                        case DISMISS_DIALOG:
                            dismissDialog();
                            break;
                        default:
                            break;
                    }
                }
            };

    /** loading dialog */
    protected void showPleaseDialog() {
        if (!isDetached()) {
            dismissDialog();
            dialog = new PictureLoadingDialog(getContext());
            dialog.show();
        }
    }

    /** dismiss dialog */
    protected void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            dialog = null;
            RLog.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onItemClick(boolean isCameraFolder, String folderName, List<LocalMedia> images) {
        boolean camera = config.isCamera ? isCameraFolder : false;
        adapter.setShowCamera(camera);
        folderWindow.dismiss();
        adapter.bindImagesData(images);
        mPictureRecycler.smoothScrollToPosition(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_CAMERA:
                    requestCamera(data);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 拍照后处理结果
     *
     * @param data
     */
    private void requestCamera(Intent data) {
        // on take photo success
        String mimeType = null;
        long duration = 0;
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        if (TextUtils.isEmpty(cameraPath)) {
            return;
        }
        long size = 0;
        int[] newSize = new int[2];
        final File file = new File(cameraPath);
        if (!isAndroidQ) {
            new PictureMediaScannerConnection(
                    getActivity(),
                    cameraPath,
                    new PictureMediaScannerConnection.ScanListener() {
                        @Override
                        public void onScanFinish() {
                            // do nothing
                        }
                    });
        }
        LocalMedia media = new LocalMedia();
        mimeType = PictureMimeType.fileToType(file);

        if (PictureMimeType.eqImage(mimeType)) {
            int degree = PictureFileUtils.readPictureDegree(getActivity(), cameraPath);
            PictureFileUtils.rotateImage(degree, cameraPath);
            newSize = MediaUtils.getLocalImageWidthOrHeight(cameraPath);
        } else {
            newSize = MediaUtils.getLocalVideoSize(cameraPath);
            duration = MediaUtils.extractDuration(getContext(), false, cameraPath);
        }
        media.setDuration(duration);
        media.setWidth(newSize[0]);
        media.setHeight(newSize[1]);
        media.setPath(cameraPath);
        media.setMimeType(mimeType);
        media.setSize(PictureFileUtils.getMediaSize(getContext(), cameraPath));
        media.setChooseModel(config.chooseMode);
        if (adapter != null) {
            if (config.selectionMode == PictureConfig.SINGLE) {
                // 单选模式
                if (config.isSingleDirectReturn) {
                    cameraHandleResult(media, mimeType);
                } else {
                    // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                    images.add(0, media);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    mimeType = selectedImages.size() > 0 ? selectedImages.get(0).getMimeType() : "";
                    boolean mimeTypeSame =
                            PictureMimeType.isMimeTypeSame(mimeType, media.getMimeType());
                    // 类型相同或还没有选中才加进选中集合中
                    if (mimeTypeSame || selectedImages.size() == 0) {
                        singleRadioMediaImage();
                        selectedImages.add(media);
                        adapter.bindSelectImages(selectedImages);
                    }
                }
            } else {
                // 多选模式
                images.add(0, media);
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                // 没有到最大选择量 才做默认选中刚拍好的
                if (selectedImages.size() < config.maxSelectNum) {
                    selectedImages.add(media);
                    adapter.bindSelectImages(selectedImages);
                } else {
                    ToastUtils.s(
                            getActivity(),
                            getString(R.string.rc_picture_message_max_num_fir)
                                    + config.maxSelectNum
                                    + getString(R.string.rc_picture_message_max_num_sec));
                }
            }

            // 规避IndexOutOfBoundException异常
            adapter.bindImagesData(images);

            // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE，不及时刷新问题手动添加
            manualSaveFolder(media);
            onPictureClick(media, 0);
        }
    }
    /** 单选图片 */
    private void singleRadioMediaImage() {
        List<LocalMedia> selectImages = adapter.getSelectedImages();
        if (selectImages != null && selectImages.size() > 0) {
            selectImages.clear();
        }
    }

    /**
     * 摄像头后处理方式
     *
     * @param media
     * @param mimeType
     */
    private void cameraHandleResult(LocalMedia media, String mimeType) {
        // 如果是单选 拍照后直接返回
        // 不裁剪 不压缩 直接返回结果
        List<LocalMedia> result = new ArrayList<>();
        result.add(media);
        onResult(result);
    }
    /**
     * 如果没有任何相册，先创建一个最近相册出来
     *
     * @param folders
     */
    protected void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = getString(R.string.rc_picture_camera_roll);
            newFolder.setName(folderName);
            newFolder.setFirstImagePath("");
            folders.add(newFolder);
        }
    }


    /**
     * 将图片插入到相机文件夹中
     *
     * @param path
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }


    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     *
     * @param media
     */
    private void manualSaveFolder(LocalMedia media) {
        try {
            createNewFolder(foldersList);
            LocalMediaFolder folder = getImageFolder(media.getPath(), foldersList);
            LocalMediaFolder cameraFolder = foldersList.size() > 0 ? foldersList.get(0) : null;
            if (cameraFolder != null && folder != null) {
                // 相机胶卷
                cameraFolder.setFirstImagePath(media.getPath());
                cameraFolder.setImages(images);
                cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
                // 拍照相册
                int num = folder.getImageNum() + 1;
                folder.setImageNum(num);
                folder.getImages().add(0, media);
                folder.setFirstImagePath(cameraPath);
                folderWindow.bindFolder(foldersList);
            }
        } catch (Exception e) {
            io.rong.common.RLog.e(TAG, e.getMessage());
        }
    }

}
