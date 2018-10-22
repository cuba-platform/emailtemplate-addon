com_haulmont_addon_emailtemplates_web_toolkit_ui_unlayereditorcomponent_UnlayerEditorComponent = function() {
    var connector = this;
        var element = connector.getElement();
        $(element).html("<div id=\"editor-container\" style=\"height:100%\"></div>");
        $(element).css("width", "100%");
        $(element).css("height", "100%");

        unlayer.init({
          id: 'editor-container',
          displayMode: 'web'
        });

        unlayer.addEventListener('design:updated', function(data) {
          unlayer.exportHtml(function(data) {
            connector.valueChanged(data);
          })
        });

        unlayer.addEventListener('design:loaded', function(data) {
          unlayer.exportHtml(function(data) {
             connector.valueChanged(data);
          })
        })

        connector.onStateChange = function() {
          var state = connector.getState();
          unlayer.setMergeTags(state.parameters);
          unlayer.loadDesign(state.json);
        }
}