"use strict";

var smth = ko.observableArray([]);

function SomeViewModel() {
    var self = this;
    self.students = ko.observableArray([]);
    $.ajax({
        Accepts:"application/json",
        url: "http://localhost:8000/app/students"
    }).then(function(data) {
        var mappedStudents = ko.mapping.fromJS(data);
        self.students(mappedStudents);
    });
    
    self.saveStudent = function() { 
        $.ajax("http://localhost:8000/app/students", {
           data: ko.mapping.toJS(smth),
            type: "post", 
console.log(type)
            contentType: "application/json",  
            success: function(result) { alert(result) },
//            statusCode: {
//                500: function() {
//                    alert( "page not found" );
//                }
//            }
        });
    };
    
    self.deleteStudent = function() { 
        alert("w");
        var ur = "http://localhost:8000/app/students/2";
        
        $.ajax({
            url: ur, 
            type: "DELETE", 
            success: function(result) { alert(result) }
        });
    };
}

ko.applyBindings(new SomeViewModel());