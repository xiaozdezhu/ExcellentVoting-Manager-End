/**
 * Created by dongsj on 16/3/20.
 */

jQuery(function(){

    $('#employee_vote').ace_spinner({
        value:0,
        min:0,
        max:9999999,
        step:1,
        //on_sides: true,
        //icon_up:'ace-icon fa fa-plus bigger-110',
        //icon_down:'ace-icon fa fa-minus bigger-110',
        //btn_up_class:'btn-success' ,
        //btn_down_class:'btn-danger'
        touch_spinner: true,
        icon_up:'ace-icon fa fa-caret-up bigger-110',
        icon_down:'ace-icon fa fa-caret-down bigger-110'
    });

    // 图片上传
    $('#employee_avatar').ace_file_input({
        style:'well',
        btn_choose:'拖入图片或点击上传图片',
        btn_change:'点击更换图片',
        no_icon:'ace-icon fa fa-picture-o',
        droppable:true,
        thumbnail:'fit',//small | large | fit
        maxSize: 1024*1024,
        allowExt: ["jpeg", "jpg", "png", "gif" ,"bmp"], // "jpeg", "jpg", "png", "gif" ,"bmp"
        allowMime: ["image/jpg", "image/jpeg", "image/png", "image/gif", "image/bmp"], // "image/jpg", "image/jpeg", "image/png", "image/gif", "image/bmp"
        // icon_remove:null, //set null, to hide remove/reset button
        before_change:function(files, dropped) {
            //Check an example below
            //or examples/file-upload.html
            console.log("dropped: " + dropped + ", files: " + files.length);
            return true;
        },
        before_remove : function() {
            console.log("remove");
            return true;
        },
        preview_error : function(filename, error_code) {
            //name of the file that failed
            //error_code values
            //1 = 'FILE_LOAD_FAILED',
            //2 = 'IMAGE_LOAD_FAILED',
            //3 = 'THUMBNAIL_FAILED'
            //alert(error_code);
            console.log("filename: " + filename + ", errorCode: " + error_code);
        }

    }).on('change', function(){
        var fileList = $(this).data('ace_input_files');
        if (fileList && fileList.length > 0 ) {
            console.log(fileList[0].size);
        }
//            console.log($(this).data('ace_input_files'));
//            console.log($(this).data('ace_input_method'));
    });



    $('#employee-info-save-btn').on('click', function(){
        if (!$('#employee-form').valid()) {
            console.log("form check failed.")
        } else {
            console.log("form check success, will to add new clerk");
            $('form#employee-form').submit(function(event){
                event.preventDefault();
                var departmentId = $('#department_id').val();
                var formData = new FormData($(this)[0]);
                $.ajax({
                    url: '/employee/save',
                    type: 'POST',
                    data: formData,
                    async: false,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (response) {
                        console.log("New clerk Case saved.");
                        bootbox.alert({
                            message: "<strong><i class='ace-icon fa fa-check bigger-200 green'></i>&nbsp;操作成功!</strong>",
                            callback: function() {
                                location.href = "/department/"+departmentId;
                            }
                        });
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        bootbox.alert({
                            message: "<strong><i class='ace-icon fa fa-warning yellow bigger-200'></i>&nbsp;操作失败!</strong>",
                            callback: function() {
                                location.href = "/department/"+departmentId;
                            }
                        });
                    }
                });

                return false;
            });
            console.log("submit");
        }
    })


});