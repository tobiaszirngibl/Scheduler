$(function () {

    jQuery.ajaxSettings.traditional = true;


    // Initiate Lists
    $(".chzn-select").chosen({
        width: "100%"
    });


    // Send CSR Token for Ajax
    var csrftoken = $.cookie('csrftoken');

    function csrfSafeMethod(method) {
        return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
    }

    $.ajaxSetup({
        beforeSend: function (xhr, settings) {
            if (!csrfSafeMethod(settings.type) && !this.crossDomain) {
                xhr.setRequestHeader("X-CSRFToken", csrftoken);
            }
        }
    });


    // Initiate Search Function
    //// Add Keyup Function for Searchbar
    $('#navbar-search-input').on('keyup', function () {
        var input, filter, ul, li, a, i;

        input = document.getElementById('navbar-search-input');
        filter = input.value.toUpperCase();
        ul = document.getElementById("userSearchList");
        li = ul.getElementsByTagName('li');

        $("#userSearch").show();
        $(".content-wrapper").hide();

        for (i = 0; i < li.length; i++) {
            a = li[i].getElementsByTagName("a")[0];
            if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
                li[i].style.display = "";
            } else {
                li[i].style.display = "none";
            }
        }
        if (input.value === "") {
            $("#userSearch").hide();
            $(".content-wrapper").show();
        }
    });


    $("#showDeleteModal").click(function () {
        $("#deleteUserMod").modal("show");
    });


    $("#deleteUserButton").click(function () {


        $.ajax({
            type: "DELETE",
            url: "http://localhost:8000/api/actor/" + user_id + "/",
            success: function (data) {
                window.location.replace("/login");
            }
        });
    });


     //// Config Result List
    $.ajax({
        type: "GET",
        url: "http://localhost:8000/api/actor/",
        success: function(data)
            {
                for (var key in data) {
                    if(data[key].first_name ==  "" && data[key].last_name == "") {
                        var actorName = "Kein Name angegeben";
                    }
                    else {
                        var actorName = data[key].first_name + " " + data[key].last_name;
                    }
                    var actorEmail = data[key].email;
                    $('#userSearchList').append('<li><a id='+data[key].id+' class="userSearchResult">'+actorEmail+' - ' + actorName +'</a></li>');
                }
            }
    });

      $("#userSearchList").on("click", ".userSearchResult", function(e){
        var userID = e.currentTarget.id;

        $.ajax({
            type: "GET",
            url: "http://localhost:8000/api/actor/"+userID+"/",
            success: function (data) {
                $("#modalUserName").text(data.first_name +" "+ data.last_name);
                $("#modalUserMail").text(data.email);
                $("#modalUserTel").text(data.phone);
                $("#modalUserNote").text(data.contact_notes);
                $("#modalUserPlace").text(data.location);
                $("#modalUserSkills").text(data.skills);
                $("#modalUserSpareTime").text(data.spare_time);
                $("#modalUserEducation").text(data.education);
                $("#modalUserJob").text(data.job);
                if(data.understudy != null ){
                    $("#modalUserUnderstudy").text(data.understudy.first_name + " " + data.understudy.last_name + " - " + data.understudy.email);
                } else {
                    $("#modalUserUnderstudy").text("");
                }
                $("#detailProfileAvatar").attr("src", data.avatar);
            }
        });

        $("#userModal").modal('show');
    });

    $("#userSearch").hide();
});