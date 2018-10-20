com_haulmont_addon_emailtemplates_web_toolkit_ui_unlayereditorcomponent_UnlayerEditorComponent = function() {
    var connector = this;
        var element = connector.getElement();
        $(element).html("<div id=\"editor-container\" style=\"height:100%\"></div>");
        $(element).css("width", "100%");
        $(element).css("height", "100%");

        unlayer.init({
          id: 'editor-container',
          displayMode: 'email'
        });

        unlayer.addEventListener('design:updated', function(data) {
          var type = data.type; // body, row, content
          var item = data.item;
          unlayer.exportHtml(function(data) {
            connector.valueChanged(data);
          })
        });

        connector.onStateChange = function() {
          var state = connector.getState();
          unlayer.loadDesign(state.json);
          unlayer.exportHtml(function(data) {
             connector.valueChanged(data);
          })
        }
}