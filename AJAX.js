/**
 * @fn ajax_request(request,params_array,action_address="NoteBooks.xml",request_type="POST")
 * framework for implementing ajax operations.
 * Requests asynchronously.
 * @param request action to be performed by processing script
 * @param params_array array of the parameters req for executing the request
 * @param request_type "POST" or "GET" here by default "POST"
 * @param action_address the address of the scripting page here by default "Processor.php"
 * @param function_params functions to be called after the response is ready and status is "OK",
 * The parameter for each function is the xml_http_request object
 * */
function ajax_request(request, params_array, function_params, action_address, request_type) {

    action_address = action_address == undefined ? "Processor.php" : action_address;
    request_type = request_type == undefined ? "POST" : request_type;

    var xml_http_request = new XMLHttpRequest();

    xml_http_request.onreadystatechange = function () {
        if(this.status == 404)alert("Server Not Found");
        else if (this.readyState == 4 && this.status == 200) {
            // alert("response is good!!");
            for(var j=0;j<function_params.length;++j){
                function_params[j](this);
            }
        }
    };

    // TODO : IMPLEMENT GET ALSO

    // POST
    xml_http_request.open(request_type, action_address, true);
    xml_http_request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    // Preparing the passing_params_array
    var formatted_params_string = "";
    /**<formatted string to pass the parameters*/
    var no_key_value_pairs = params_array.length;
    for (var i = 0; i < no_key_value_pairs; ++i) {
        if (i % 2 == 0)formatted_params_string += params_array[i] + "=";
        else formatted_params_string += params_array[i] + "&";
    }
    formatted_params_string += "form_type=" + request;
    // alert(formatted_params_string);
    xml_http_request.send(formatted_params_string);
}