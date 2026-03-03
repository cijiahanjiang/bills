// let domain = "http://127.0.0.1:8088"
let domain = "http://127.0.0.1:8080"
let editor;
let addRow;
let table;
let tableColumnsDef
let owner;

let myChart1
let myChart2
let myChart3
let myChart4
let myChart5

let option1
let option2
let option3
let option4
let option5

let selectedColumn;
let rowMode;
let columMode;
let elementMode = true;

$(document).ready(function () {
    $("#username").val('beibei')
    initQueryOptions()
    initTime()
    initChart()
    initTable()
    table.select.style('api')
    query()
})


function deleteRow(id) {
    let url = domain + '/bill/records/delete?id=' + id + "&username=" + $("#username").val()
    $.get({
        url: url,
        async: false
    })
    query()
}

function editRow(id) {
    // 根据id找到对应的行
    const targetRow = table.row("#" + id);
    if (targetRow.length) {
        editor.edit(targetRow.index(), {
            title: '编辑',
            buttons: '保存',
            formOptions: {
                main: {
                    scope: 'row'
                }
            }
        });
    }
}

function copyRow(id) {
    console.log(table.rows().ids())
    const targetRow = table.row("#" + id);
    if (targetRow.length) {
        // 获取当前行的原始数据
        const rowData = targetRow.data();
        // 解析交易时间
        const originalTime = new Date(rowData.dealTime);
        // 时间加一个月
        originalTime.setMonth(originalTime.getMonth() + 1);
        // 格式化新的时间
        const newDealTime = formatDate(originalTime);
        // 打开复制弹窗，设置新的交易时间
        editor.edit(targetRow.index(), {
            title: '复制',
            buttons: '保存',
            formOptions: {
                main: {
                    scope: 'row'
                }
            }
        }).mode('create')
            .set('dealTime', newDealTime);
    }
}

// 格式化时间为YYYY-MM-DD HH:mm:ss格式
function formatDate(date) {
    let year = date.getFullYear();
    let month = String(date.getMonth() + 1).padStart(2, '0');
    let day = String(date.getDate()).padStart(2, '0');
    let hours = String(date.getHours()).padStart(2, '0');
    let minutes = String(date.getMinutes()).padStart(2, '0');
    let seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

function buildUlr(method) {
    return domain + method;
}

function updateItems() {
    if (window.event.keyCode === 13) {
        let url = domain + '/bill/items/update?items=' + $("#items").val() + "&username=" + $("#username").val()
        $.get(url)
        query()
    }
}

function refreshCharts() {
    $("#charts").css('display', 'block')
    $.ajax({
        url: buildUlr("/bill/stat"),
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({
            stime: $("#stime").val(),
            etime: $("#etime").val(),
            username: $("#username").val()
        }),
        type: 'POST'
    }).then(function (data) {
        option1.xAxis.data = data.months;
        option1.series[0].data = data.monthIncomes
        option1.series[1].data = data.monthOutcomes
        option2.series[0].data = data.outcomeTopTypes
        option3.series[0].data = data.outcomeTags
        option4.series[0].data = data.outcomeNecessity
        option5.series[0].data = data.incomeTypes

        myChart1.setOption(option1);
        myChart2.setOption(option2);
        myChart3.setOption(option3);
        myChart4.setOption(option4);
        myChart5.setOption(option5);
    })
}

function query() {
    $.get(domain + '/bill/items?username=' + $("#username").val(), function (data) {
        $("#items").val(data)
    })
    refreshCharts()
    table.page(table.page()).draw('page')
}

function initUserData() {
    if (window.event.keyCode === 13) {
        let url = domain + '/bill/items?username=' + $("#username").val()
        $.get(url, function (data) {
            $("#items").val(data)
        })
        query()
    }
}

function getDateStr(date) {
    let month = date.getMonth() + 1;
    let day = date.getDate();
    if (day < 10) {
        day = "0" + day;
    }
    if (month < 10) {
        return date.getFullYear() + "-0" + month + "-" + day;
    } else {
        return date.getFullYear() + "-" + month + "-" + day;
    }
}

function uploadFile() {
    let files = $('#records_file').prop('files');
    let data = new FormData();
    data.append('file', files[0]);
    let url = domain + '/bill/uploadRecords?username=' + $("#username").val() + "&channel=" + $("#channel").find('option:selected').val()
    $.ajax({
        url: url,
        type: 'POST',
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {
            return data;
        },
        sync: false
    }).then(function (rsp) {
        query()
    });
}

function buildUpdateParam(data) {
    let param = new Object()
    param.ids = Object.keys(data.data);
    let rowData = data.data[param.ids[0]]
    rowData.action = data.action
    //单元素、列编辑只返回单列值
    if (elementMode || rowMode) {
        rowData.ids = param.ids
        return JSON.stringify(rowData);
    } else if (columMode) {
        param.action = data.action
        param[selectedColumn] = rowData[selectedColumn]
        return JSON.stringify(param);
    }
}

function buildAddParam(data) {
    let param = new Object()
    param.ids = Object.keys(data.data);
    let rowData = data.data[param.ids[0]]
    rowData.username = $("#username").val();
    rowData.action = data.action;
    rowData.ids = param.ids
    return JSON.stringify(rowData);
}

function initTable() {
    initTableData()
    editor = new $.fn.dataTable.Editor({
        //使用自定义ajax，服务端返回的数据更通用，后续可以做移动端
        ajax: function (method, url, data, successCallback, errorCallback) {
            if ('create' === data.action) {
                table.select.style('api')
            }
            $.ajax({
                url: buildUlr("/bill/records/upsert"),
                contentType: "application/json",
                data: 'create' === data.action ? buildAddParam(data) : buildUpdateParam(data),
                sync: false,
                type: 'post',
            }).then(function (d) {
                $.post({
                    url: buildUlr("/bill/records"),
                    data: buildEditQuery(),
                    contentType: "application/json",
                    success: function (result) {
                        refreshCharts()
                        let info = table.page.info();
                        result.recordsTotal = result.total
                        result.recordsFiltered = result.total
                        result.draw = info.draw
                        successCallback(result);
                    }
                })
            })
        },
        table: '#bill',
        idSrc: 'id',
        formOptions: {
            main: {
                scope: 'cell' // Allow multi-row editing with cell selection
            }
        },
        fields: [
            // {name: 'id', label: 'id', visible: false},
            {label: '收支', name: 'flowType', type: 'select'},
            {label: '金额', name: 'amount'},
            {label: '周期性', name: 'recurring', type: 'select'},
            {label: '分类', name: 'topType', type: 'select'},
            {
                label: '事项', name: 'items', type: 'checkbox',
            },
            {label: '必要性', name: 'necessity', type: 'select'},
            {
                label: '交易时间', name: 'dealTime', type: 'datetime', format: "YYYY-MM-DD HH:mm:ss", opts: {
                    timeFormat: 'HH:mm:ss',
                    timePicker: true,
                    timePicker24Hour: true,
                    timePickerIncrement: 1,      // 分钟间隔
                    secondsAvailable: true         // 启用秒选择
                }
            },
            {label: '产品', name: 'product'},
            {label: '地点', name: 'location'},
            {label: '交易方', name: 'dealer'},
            {label: '交易类型', name: 'dealType'},
        ],
        i18n: {
            datetime: {
                previous: '上',
                next: '下',
                months: [
                    '一月',
                    '二月',
                    '三月',
                    '四月',
                    '五月',
                    '六月',
                    '七月',
                    '八月',
                    '九月',
                    '十月',
                    '十一月',
                    '十二月',
                ],
                weekdays: ['周一', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
            },
            "multi": {
                "title": "列编辑",
                "info": "选择的列包含多个值，重新选择后将更新为相同的值",
                "restore": "",
                "noMulti": ""
            },
            "edit": {
                "button": "更新",
                "title": "批量更新",
                "submit": "提交"
            }
        }
    })
    table = $('#bill').DataTable({
        language: {
            emptyTable: "无数据",
            search: "关键词搜索",
            paginate: {
                previous: "上一页",
                next: "下一页"
            }
        },
        idSrc: 'id',
        rowId: 'id',
        dom: "<'bmargin-left'B>ptpi",
        buttons: [
            {
                extend: 'createInline', editor, formOptions: {
                    submitTrigger: -1,
                    submitHtml: '<button class="bbutton">保存</button>'
                },
                text: '<span>新增</span>'
            },
            {
                text: '行内编辑', action: function (e, dt, node, config) {
                    table.select.style('api')
                    elementMode = true
                    rowMode = false
                    columMode = false
                    table.page(table.page()).draw('page')
                }
            },
            {
                extend: 'selectColumns', editor, text: '列编辑',
                action: function (e, dt, node, config) {
                    table.select.style('os')
                    elementMode = false
                    rowMode = false
                    columMode = true
                    table.page(table.page()).draw('page')
                    $.fn.dataTable.ext.buttons.selectColumns.action.call(this, e, dt, node, config);
                }
            },
            {extend: 'edit', editor, text: '编辑'},
            {extend: 'selectNone', text: '清除'}
        ],
        columns: tableColumnsDef,
        paging: true,
        pageLength: 20,
        search: false,
        serverSide: true,
        ordering: false,
        info: '',
        infoEmpty: "",
        rowReorder: {
            dataSrc: 'sequence',
            editor: null
        },
        select: true,
        //使用自定义ajax，服务端返回的数据更通用，后续可以做移动端
        ajax: function (data, callback, settings) {
            console.log(data)
            $.post({
                url: buildUlr("/bill/records"),
                data: buildTableDataRequest(data),
                contentType: "application/json",
                success: function (result) {
                    result.recordsTotal = result.total
                    result.recordsFiltered = result.total
                    result.draw = data.draw
                    callback(result);
                }
            })
        },
    });

    table.on('select', function (e, dt, type, indexes) {
        selectedColumn = tableColumnsDef[indexes].data;
    });

    $('#bill tbody').on('click', 'td:not(:last-child)', function (e) {
        if (elementMode) {
            editor.inline(this);
        }
    });
}

function buildTableDataRequest(data) {
    let request = getQueryCondition();
    request.offset = data.start
    request.page = data.page
    request.size = data.length;
    return JSON.stringify(request);
}

function getQueryCondition() {
    let request = new Object()
    request.username = $("#username").val()
    request.stime = $("#stime").val()
    request.etime = $("#etime").val()
    request.product = $("#product").val()
    request.dealer = $("#dealer").val()
    request.minAmount = $("#minAmount").val()
    request.maxAmount = $("#maxAmount").val()
    request.flowType = $("#flowTypeCondition").find('option:selected').val()
    request.recurring = $("#recurringCondition").find('option:selected').val()
    request.necessity = $("#necessityCondition").find('option:selected').val()
    request.items = $("#itemsCondition").find('option:selected').val()
    request.topType = $("#topTypeCondition").find('option:selected').val()
    return request;
}

function buildEditQuery() {
    let request = getQueryCondition();
    if (table.hasOwnProperty('page')) {
        let pageInfo = table.page.info();
        request.offset = pageInfo.start
        request.page = pageInfo.page
        request.size = pageInfo.length
    }
    return JSON.stringify(request);
}

function initTime() {
    let stime = new Date();
    let etime = new Date();
    stime.setDate(stime.getDate() - 410);
    etime.setDate(etime.getDate() - 46);
    // $("#stime").val(getDateStr(stime))
    // $("#etime").val(getDateStr(etime))
    $("#stime").val('2026-01-01')
    $("#etime").val('2026-03-31')
}

function initChart() {
    initCharData()
    // 基于准备好的dom，初始化echarts实例
    myChart1 = echarts.init(document.getElementById('main1'));
    myChart2 = echarts.init(document.getElementById('main2'));
    myChart3 = echarts.init(document.getElementById('main3'));
    myChart4 = echarts.init(document.getElementById('main4'));
    myChart5 = echarts.init(document.getElementById('main5'));
    $("#charts").css('display', 'none')
}

function initCharData() {
    option1 = {
        title: {
            text: '收支统计'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['收入', '支出']
        },
        xAxis: {
            data: []
        },
        yAxis: {},
        series: [{
            name: '收入',
            type: 'line',
            data: []
        },
            {
                name: '支出',
                type: 'line',
                data: []
            }]
    };
    option2 = {
        title: {
            text: '分类'
        },
        tooltip: {},
        series: [
            {
                type: 'pie',
                radius: '70%',
                data: []
            }
        ]
    };
    option3 = {
        title: {
            text: '事项'
        },
        tooltip: {},
        series: [
            {
                type: 'pie',
                radius: '70%',
                data: []
            }
        ]
    };
    option4 = {
        title: {
            text: '合理性'
        },
        tooltip: {},
        series: [
            {
                type: 'pie',
                radius: '70%',
                data: []
            }
        ]
    };
    option5 = {
        title: {
            text: '分类收入'
        },
        tooltip: {},
        series: [
            {
                type: 'pie',
                radius: '70%',
                data: []
            }
        ]
    };
}

function initTableData() {
    tableColumnsDef = [
        {data: "id", visible: false},
        {data: "flowType", editField: 'flowType', width: '80px'},
        {data: "amount"},
        {data: "recurring", width: '80px'},
        {data: "topType", width: '80px'},
        {data: 'items', render: '[,]', editField: 'items', width: '100px'},
        {data: "necessity", editField: "necessity", width: '80px'},
        {data: "product", width: '200px'},
        {data: "location", width: '80px'},
        {data: "dealTime", width: '200px'},
        {data: "dealer", width: '130px'},
        {data: "dealType", width: '130px'},
        {
            data: null, width: '200px', render: function (data, type, row, meta) {
                return '<button class="bbutton" onclick="editRow(' + row.id + ')">编辑</button> ' +
                    '<button class="bbutton" onclick="copyRow(' + row.id + ')">复制</button> ' +
                    '<button class="bbutton" onclick="deleteRow(' + row.id + ')">删除</button>';
            }
        }
    ]
}

function initQueryOptions() {
    let url = domain + '/bill/getSelectOptions?username=' + $("#username").val()
    $.get({
        url: url,
        async: false
    }).then(function (data) {
        data.topType.forEach(function (item) {
            $('#topTypeCondition').append($('<option></option>').val(item).text(item));
        });
        data.items.forEach(function (item) {
            $('#itemsCondition').append($('<option></option>').val(item).text(item));
        });
        data.recurring.forEach(function (item) {
            $('#recurringCondition').append($('<option></option>').val(item).text(item));
        });
        data.flowType.forEach(function (item) {
            $('#flowTypeCondition').append($('<option></option>').val(item).text(item));
        });
        data.necessity.forEach(function (item) {
            $('#necessityCondition').append($('<option></option>').val(item).text(item));
        });
    })
}