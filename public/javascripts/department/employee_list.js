/**
 * Created by dongsj on 16/3/20.
 */


/**
 * Created by dongsj on 16/1/16.
 */

var selectGender = -1;
var selectStatus = -1;
var sortOrder = -1;

// 选择
$('#employee_gender_select').change(function(){
    var ps = $('#employee-table-select-pagesize').val();
    var pn = 1;
    var keyword = $('#employee-search-input').val();
    
    selectGender = $(this).val();
    flushEmployeeTableData(ps, pn, keyword);
});

// 选择
$('#employee_status_select').change(function(){
    var ps = $('#employee-table-select-pagesize').val();
    var pn = 1;
    var keyword = $('#employee-search-input').val();

    selectStatus = $(this).val();
    flushEmployeeTableData(ps, pn, keyword);
});

// 选择
$('#employee_vote_select').change(function(){
    var ps = $('#employee-table-select-pagesize').val();
    var pn = 1;
    var keyword = $('#employee-search-input').val();

    sortOrder = $(this).val();
    flushEmployeeTableData(ps, pn, keyword);
});



// 页面大小选择
$('#employee-table-select-pagesize').change(function(){
    console.log("employee table select pagesize is : ", $(this).val());
    var ps = $(this).val();
    var pn = 1;
    var keyword = $('#employee-search-input').val();
    flushEmployeeTableData(ps, pn, keyword);
});

// 搜索
$('#employee-search-input').on("keydown", function(event){
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==13 ){ // enter 键
        var keyword = $(this).val();
        console.log("employee Search: " + keyword);
        var ps = $('#employee-table-select-pagesize').val();
        var pn = 1;
        flushEmployeeTableData(ps, pn, keyword);
    }
});

// 上一页
function employeeTablePreviousClick(){
    console.log("employee table press previous page.");
    var ps = $('#employee-table-select-pagesize').val();
    // 获取当前选中页码
    var pn = $('ul#employee-table-paginate li.active').attr('pn');
    var keyword = $('#employee-search-input').val();
    console.log(ps + " " + (parseInt(pn)-1) + " " + keyword);
    flushEmployeeTableData(ps, (parseInt(pn)-1), keyword);
}

// 下一页
function employeeTableNextClick() {
    console.log("employee table press next page.");
    var ps = $('#employee-table-select-pagesize').val();
    // 获取当前选中页码
    var pn = $('ul#employee-table-paginate li.active').attr('pn');
    var keyword = $('#employee-search-input').val();
    console.log(ps + " " + (parseInt(pn)+1) + " " + keyword);
    flushEmployeeTableData(ps, (parseInt(pn)+1), keyword);
}

// 页码选择
function employeeTablePageSelect(selectedPage) {
    console.log("select page is " + $(selectedPage).attr('pn'));
    var ps = $('#employee-table-select-pagesize').val();
    var pn = $(selectedPage).attr('pn');
    var keyword = $('#employee-search-input').val();
    flushEmployeeTableData(ps, pn, keyword);
}

// 编辑
function toEditEmployee(employeeId) {
    console.log("Edit employee: " + employeeId);
    location.href = "/employee/edit/" + employeeId;
}

// 删除
function toDeleteEmployee(departmentId, employeeId) {
    bootbox.confirm({
        buttons: {
            "cancel" : {
                "label" : "取消",
                "className" : "btn-sm",
            },
            "confirm": {
                "label": "确定",
                "className" : "btn-sm btn-success",
            }
        },
        message: "<strong><i class='ace-icon fa fa-warning yellow bigger-200'></i>&nbsp;确定要删除该候选人吗?</strong>",
        callback: function(result) {
            if (result) {
                $.ajax({
                    type : "POST",
                    url: "/employee/delete/" + employeeId,
                    contentType: "application/json",
                    dataType: "json",
                    data: "",
                    success: function(response) {
                        console.log(response);
                        bootbox.alert({
                            message: "<strong><i class='ace-icon fa fa-check bigger-200 green'></i>&nbsp;删除成功!</strong>",
                            callback: function() {
                                location.href = "/department/"+departmentId;
                            }
                        });
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        bootbox.dialog({
                            message: "<strong><i class='ace-icon fa fa-warning yellow bigger-200'></i>&nbsp;删除失败!</strong>",
                        });
                    }
                });
            }
        }
    });
}

// =================================================================================================================


function flushEmployeeTableData(ps, pn, keyword) {
    var departmentId = $('#department-employee-title').attr('departmentId');
    $.ajax({
        type : "POST",
        url: "/employee/query/"+ps+"/"+pn,
        contentType: "application/json",
        dataType: "json",
        data: "{\"keyword\": \""+keyword+"\", \"isDelete\": -1, \"departmentId\": "+departmentId+", \"gender\": "+selectGender+",\"status\": "+selectStatus+", \"sortOrder\": "+sortOrder+"}",
        success: function(response) {
            console.log(response);
            genEmployeeTableData(response);
        }
    });
};

/**
 * 生成业主信息 替换业主信息
 * @param response 业主信息
 */
function genEmployeeTableData(response) {
    var genders = ["", "男", "女"];
    var employeeList = response.employeeList;

    // table body
    var tbody = '<tbody id="employee-table-body">';
    for(i=0; i<employeeList.length; i++) {

        var employee = employeeList[i];

        var editBtn = ' <button class="btn btn-xs btn-info tooltip-info" data-rel="tooltip" title="编辑" onclick="toEditEmployee('+ employee.id +')"> \
                                <i class="ace-icon fa fa-edit bigger-120"></i>\
                            </button>';
        var deleteBtn = '  <button class="btn btn-xs btn-danger tooltip-error" data-rel="tooltip" data-placement="top" title="删除" onclick="toDeleteEmployee('+employee.departmentId+','+ employee.id +')"> \
                                <i class="ace-icon fa fa-trash bigger-120"></i> \
                            </button>';

        var statusView = '';
        if (0 == employee.status) {
            statusView = '<span class="label label-lg label-default arrowed-in arrowed-right">未审核</span>';
        } else if (1 == employee.status) {
            statusView = '<span class="label label-lg label-success arrowed-in arrowed-right">审核通过</span>';
        } else if (2 == employee.status) {
            statusView = '<span class="label label-lg label-inverse arrowed-in arrowed-right">审核未通过</span>';
        }
        
        var tr =
            '<tr>   \
                <td class="center">'+ employee.name +'</td> \
                <td class="center">\
                    <img src="/public/images/employee/'+employee.avatar+'" width="50" height="50"/>\
                </td> \
                <td class="center">'+ genders[employee.gender] +'</td> \
                <td class="center">'+ employee.position +'</td> \
                <td class="center">'+ employee.description +'</td> \
                <td class="center">'+ employee.votes +'</td> \
                <td class="center">'+ statusView +'</td> \
                <td class="center">'+ editBtn+''+deleteBtn+'</td>\
                ';

        tr = tr + "</tr>";
        tbody = tbody + tr;
    }
    tbody = tbody + '</tbody>';

    // table paginate
    var tPaginate = '<ul id="employee-table-paginate" class="pagination"> ';
    // previous
    if (response.pn == 1 || response.maxPN == 1) {
        tPaginate = tPaginate +
            '<li class="paginate_button previous disabled"> \
                <a><i class="ace-icon fa fa-angle-double-left"></i></a> \
            </li>';
    } else {
        tPaginate = tPaginate +
            '<li class="paginate_button previous" onclick="employeeTablePreviousClick()"> \
                <a><i class="ace-icon fa fa-angle-double-left"></i></a> \
            </li>';
    }

    startPage = response.pn > 3 ? response.pn - 3 : 1;
    endPage = response.pn + 3 <= response.maxPN ? response.pn + 3 : response.maxPN;
    // page-1
    if (startPage > 1) {
        if (startPage > 2) {
            tPaginate = tPaginate +
                '<li class="paginate_button" pn="1" onclick="employeeTablePageSelect(this)"> \
                        <a>1...</a> \
                    </li>';
        } else {
            tPaginate = tPaginate +
                '<li class="paginate_button" pn="1" onclick="employeeTablePageSelect(this)"> \
                        <a>1</a> \
                    </li>';
        }
    }
    // page
    for (index=startPage; index <= endPage; index++) {
        if (index == response.pn) {
            tPaginate = tPaginate +
                '<li class="paginate_button active" pn="'+ index +'" onclick="employeeTablePageSelect(this)"> \
                    <a>'+ index +'</a> \
                </li>';
        } else {
            tPaginate = tPaginate +
                '<li class="paginate_button" pn="'+ index +'" onclick="employeeTablePageSelect(this)"> \
                    <a>'+ index +'</a> \
                </li>';
        }
    }
    // page-max
    if (endPage < response.maxPN) {
        if (endPage < response.maxPN - 1) {
            tPaginate = tPaginate +
                '<li class="paginate_button" pn="' + response.maxPN + '" onclick="employeeTablePageSelect(this)"> \
                    <a>...' + response.maxPN + '</a> \
                 </li>';
        } else {
            tPaginate = tPaginate +
                '<li class="paginate_button" pn="' + response.maxPN + '" onclick="employeeTablePageSelect(this)"> \
                    <a>' + response.maxPN + '</a> \
                 </li>';
        }
    }
    //for (index=1; index <= response.maxPN; index++) {
    //    if (index == response.pn) {
    //        tPaginate = tPaginate +
    //            '<li class="paginate_button active" pn="'+ index +'" onclick="selectPage(this)"> \
    //                <a>'+ index +'</a> \
    //            </li>';
    //    } else {
    //        tPaginate = tPaginate +
    //            '<li class="paginate_button" pn="'+ index +'" onclick="selectPage(this)"> \
    //                <a>'+ index +'</a> \
    //            </li>';
    //    }
    //}
    if (response.pn == response.maxPN || response.maxPN <= 1) {
        tPaginate = tPaginate +
            '<li class="paginate_button next disabled"> \
                <a><i class="ace-icon fa fa-angle-double-right"></i></a>\
            </li>';
    } else {
        tPaginate = tPaginate +
            '<li class="paginate_button next" onclick="employeeTableNextClick()"> \
                <a><i class="ace-icon fa fa-angle-double-right"></i></a>\
            </li>';
    }
    tPaginate = tPaginate + '</ul>';

    // 替换
    $('#employee-table-body').replaceWith(tbody);
    $('#employee-total').text(response.total);
    $('#employee-table-paginate').replaceWith(tPaginate);

    $('[data-rel=tooltip]').tooltip();
};

