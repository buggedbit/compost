
/**
 * @fn ajax_request(request,params_array,action_address="NoteBooks.xml",request_type="POST")
 * framework for implementing ajax operations.
 * Requests asynchronously.
 * @param request action to be performed by processing script
 * @param params_array array of the parameters req for executing the request
 * @param request_type "POST" or "GET" here by default "POST"
 * @param action_address the address of the scripting page here by default "Processor.php"
 * */
function ajax_request(request,params_array,action_address,request_type) {

    action_address = action_address == undefined ? "Processor.php" : action_address;
    request_type = request_type == undefined ? "POST" : request_type;

    var xml_http_request = new XMLHttpRequest();
    xml_http_request.onreadystatechange = function () {
        // if(this.status == 404)alert("Server Not Found");
        if(this.readyState == 4 && this.status == 200){
            // alert("response is good!!");
            useResponse(this);
        }
    };

    // TODO : IMPLEMENT GET ALSO

    // POST
    xml_http_request.open(request_type,action_address,true);
    xml_http_request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    // Preparing the passing_params_array
    var formatted_params_string = "";/**<formatted string to pass the parameters*/
    var no_key_value_pairs = params_array.length;
    for (var i=0;i<no_key_value_pairs;++i){
        if (i%2 == 0)formatted_params_string += params_array[i]+"=";
        else formatted_params_string += params_array[i]+"&";
    }
    formatted_params_string += "form_type=" + request;
    // alert(formatted_params_string);
    xml_http_request.send(formatted_params_string);
}

/**
 * @fn useResponse(response)
 * Uses response of an ajax request .
 * The ajax_request(request,params_array,action_address="NoteBooks.xml",request_type="POST")
 * has the listener to state changes and when the response is ready and the status is "OK" this function is called
 *
 * Implement this to use the response of an ajax response
 *
 * @param response the xmlHTTP object returned after response is ready and status is "OK"
 * */
function useResponse(response) {
    alert(response.responseText);
}