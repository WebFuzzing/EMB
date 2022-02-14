require 'asciidoctor'
require 'asciidoctor/extensions'

class TodoInlineMacro < Asciidoctor::Extensions::InlineMacroProcessor
  use_dsl
  
  named :todo
  name_positional_attributes 'description'
  
  def process parent, target, attrs
    %(<span class="#{target}">#{attrs['description']}</span>)
  end
end

class GlossaryMacro < Asciidoctor::Extensions::InlineMacroProcessor
  use_dsl
  
  named :gloss
  name_positional_attributes 'keyTerm'
  
  def process parent, target, attrs
    keyterm = attrs["keyTerm"] || target
    %(<abbr title="#{keyterm}">#{target}</abbr>)
  end
end

Asciidoctor::Extensions.register do
  inline_macro TodoInlineMacro
  inline_macro GlossaryMacro
end
